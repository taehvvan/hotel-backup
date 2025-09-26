package com.example.backend.search;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.Normalizer; // ★ 추가
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class HotelExcelService {

    private static final Logger log = LoggerFactory.getLogger(HotelExcelService.class);

    private final HotelRepository hotelRepository;
    private final ServiceRepository serviceRepository;
    private final RoomRepository roomRepository;
    private final HotelImageRepository hotelImageRepository;

    @Value("${file.upload-dir}")
    private String imageBaseDirectoryPath; // 예: D:/hotel_image/

    public HotelExcelService(HotelRepository hotelRepository,
                             ServiceRepository serviceRepository,
                             RoomRepository roomRepository,
                             HotelImageRepository hotelImageRepository) {
        this.hotelRepository = hotelRepository;
        this.serviceRepository = serviceRepository;
        this.roomRepository = roomRepository;
        this.hotelImageRepository = hotelImageRepository;
    }

    // ───────────────────── 유틸 ─────────────────────

    /** 헤더/키 비교용: 유니코드 정규화 + 보이지 않는 문자 제거 + 공백제거 + 소문자 */
    private String norm(String s) {
        if (s == null) return "";
        String t = Normalizer.normalize(s, Normalizer.Form.NFKC);
        // BOM/제로폭/NBSP 제거
        t = t.replaceAll("[\\u200B-\\u200D\\uFEFF\\u00A0]", "");
        t = t.replaceAll("\\s+", "").toLowerCase();
        return t;
    }

    /** 객실 타입 문자열 정규화: 괄호/인원표기 제거 + 공백제거 + 소문자 + 동의어 치환 */
    private String normType(String s){
        if (s == null) return "";
        String t = s;
        // 괄호 및 내용 제거, 인원 꼬리표 제거
        t = t.replaceAll("\\(.*?\\)", "");
        t = t.replaceAll("최대\\s*\\d+\\s*명", "");
        t = t.replaceAll("\\d+\\s*명", "");
        t = t.replaceAll("\\s+","").toLowerCase();

        // 동의어/표기 통일
        t = t.replace("온돌", "한실");     // 온돌 = 한실
        t = t.replace("한실룸", "한실");
        t = t.replace("양실룸", "양실");
        t = t.replace("western", "양실");
        return t;
    }

    /** 헤더 맵 구성 */
    private Map<String, Integer> buildHeaderMap(Row headerRow) {
        Map<String, Integer> m = new HashMap<>();
        if (headerRow == null) return m;
        for (Cell c : headerRow) {
            if (c != null && c.getCellType() == CellType.STRING) {
                m.put(norm(c.getStringCellValue()), c.getColumnIndex());
            }
        }
        return m;
    }

    /** 여러 후보 키 중 먼저 매칭되는 컬럼 인덱스 반환(없으면 -1) */
    private int col(Map<String, Integer> headerMap, String... keys) {
        for (String k : keys) {
            Integer idx = headerMap.get(norm(k));
            if (idx != null) return idx;
        }
        return -1;
    }

    /** 상세 텍스트에서 가격 파싱 (쉼표/원/KRW 허용) */
    private int parsePriceFromDetail(String detail) {
        if (detail == null || detail.isEmpty()) return 0;
        Matcher m = Pattern.compile("(\\d{1,3}(?:,\\d{3})+|\\d+)\\s*(?:원|krw)?",
                Pattern.CASE_INSENSITIVE).matcher(detail);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1).replace(",", ""));
            } catch (NumberFormatException ignore) {}
        }
        return 0;
    }

    /**
     * "타입:가격" / "타입=가격" / "타입(가격)" 포맷을 파싱.
     * 구분자: , / |
     * 예) "스탠다드:80000/디럭스:120000/스위트(220,000)"
     */
    private Map<String,Integer> parseTypeIntMap(String text){
        Map<String,Integer> map = new HashMap<>();
        if (text == null || text.trim().isEmpty()) return map;

        String[] parts = text.split("[,/|]+");Pattern kv1 = Pattern.compile(
            "^(.+?)[=:]\\s*(\\d{1,3}(?:,\\d{3})+|\\d+)\\s*(?:원|krw)?",
            Pattern.CASE_INSENSITIVE
        );
        Pattern kv2 = Pattern.compile(
            "^(.+?)\\s*\\((\\d{1,3}(?:,\\d{3})+|\\d+)\\s*(?:원|krw)?\\)",
            Pattern.CASE_INSENSITIVE
        );

        for (String raw : parts){
            String t = raw.trim();
            if (t.isEmpty()) continue;

            Matcher m = kv1.matcher(t);
            if (!m.find()) m = kv2.matcher(t);
            if (m.find()){
                String type = m.group(1).trim();
                String num  = m.group(2).replace(",", "");
                try {
                    map.put(normType(type), Integer.parseInt(num));
                } catch (NumberFormatException ignore) {}
            }
        }
        return map;
    }

    /** 타입별 가격 느슨한 매칭: 정확 일치 → (포함/역포함) 가장 긴 키 */
    private Integer lookupTypePrice(Map<String,Integer> map, String roomType) {
        if (map == null || map.isEmpty()) return null;
        String key = normType(roomType);

        // 1) 정확 일치
        Integer p = map.get(key);
        if (p != null) return p;

        // 2) 부분 일치(가장 긴 키 우선)
        String bestKey = null;
        for (String k : map.keySet()) {
            if (key.contains(k) || k.contains(key)) {
                if (bestKey == null || k.length() > bestKey.length()) {
                    bestKey = k;
                }
            }
        }
        return (bestKey != null) ? map.get(bestKey) : null;
    }

    // ───────────────────── 메인 임포트 ─────────────────────

    @Transactional
    public void importHotelsFromExcel(String filePath, String hotelType) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
                Sheet sheet = workbook.getSheetAt(s);
                if (sheet == null) continue;

                Row headerRow = sheet.getRow(0);
                Map<String, Integer> headerMap = buildHeaderMap(headerRow);

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    // ───────── 기본 정보 ─────────
                    String hotelName = getCellString(row, col(headerMap, "명칭", "이름", "name"));
                    String hotelAddress = getCellString(row, col(headerMap, "주소", "address"));
                    if (hotelName.isEmpty()) continue;

                    Hotel hotel = hotelRepository.findByhNameAndAddress(hotelName, hotelAddress)
                            .orElseGet(() -> {
                                Hotel h = new Hotel();
                                h.setHName(hotelName);
                                if (hotelAddress != null && !hotelAddress.isEmpty()) {
                                    String[] addressParts = hotelAddress.split(" ");
                                    h.setRegion(addressParts.length >= 2 ? addressParts[0] + " " + addressParts[1] : addressParts[0]);
                                }
                                h.setAddress(hotelAddress);
                                h.setLatitude(getCellDouble(row, col(headerMap, "위도", "lat", "latitude")));
                                h.setLongitude(getCellDouble(row, col(headerMap, "경도", "lng", "longitude")));
                                h.setInfo(getCellString(row, col(headerMap, "개요", "소개", "overview", "description")));
                                h.setType(hotelType);
                                if (h.getRooms() == null) h.setRooms(new ArrayList<>());
                                if (h.getImages() == null) h.setImages(new ArrayList<>());
                                return hotelRepository.save(h);
                            });

                    // ───────── 편의시설 ─────────
                    Set<ServiceEntity> serviceEntities = new HashSet<>();
                    String detailInfo = getCellString(row, col(headerMap, "상세정보", "상세", "detail", "description"));

                    String facilitiesText = getCellString(row, col(headerMap, "부대 시설", "부대시설"));
                    if (!facilitiesText.isEmpty()) {
                        String[] facilities = facilitiesText.split("[,/]");
                        for (String f : facilities) {
                            String v = f.trim();
                            if (!v.isEmpty()) serviceEntities.add(findOrCreateService(v));
                        }
                    }

                    String[] amenityHeaders = {
                            "주차 가능","조리 가능","픽업서비스","식음료장","세미나","스포츠시설","사우나실","뷰티 시설",
                            "노래방","바베큐장","캠프화이어","자전거대여","휘트니스센터","공용 PC실","공용 샤워실"
                    };
                    for (String hname : amenityHeaders) {
                        int idx = col(headerMap, hname);
                        if (idx >= 0) {
                            String v = getCellString(row, idx);
                            if ("있음".equals(v) || "가능".equals(v) || "Y".equalsIgnoreCase(v)) {
                                serviceEntities.add(findOrCreateService(hname));
                            }
                        }
                    }

                    if (!detailInfo.isEmpty()) {
                        Pattern amenityPattern = Pattern.compile("([^\\n\\r:]+?)\\s*:\\s*Y");
                        Matcher matcher = amenityPattern.matcher(detailInfo);
                        while (matcher.find()) {
                            String amenityName = matcher.group(1).trim();
                            if (!amenityName.isEmpty()) serviceEntities.add(findOrCreateService(amenityName));
                        }
                    }
                    if (!serviceEntities.isEmpty()) {
                        hotel.setServices(new ArrayList<>(serviceEntities));
                    }

                    // ───────── 공통 룸 속성 ─────────
                    String checkinTime = parseFirstTime(getCellString(row, col(headerMap, "체크인", "checkin", "입실시간")), "18:00");
                    String checkoutTime = parseFirstTime(getCellString(row, col(headerMap, "체크아웃", "checkout", "퇴실시간")), "13:00");

                    // 가격(공통가)
                    int idxPrice = col(headerMap, "가격","객실가격","요금","요금(원)","price","minprice","최저가");
                    int commonPrice = getCellInt(row, idxPrice);
                    if (commonPrice == 0) {
                        commonPrice = parsePriceFromDetail(detailInfo);
                    }

                    // 객실별 가격 맵 (퍼지 탐색 포함)
                    int idxTypePrices = col(headerMap, "객실별 가격","객실가격(타입별)","room prices","roomprices");
                    if (idxTypePrices < 0) {
                        for (Map.Entry<String,Integer> e : headerMap.entrySet()) {
                            String k = e.getKey(); // 이미 norm()된 키
                            if (k.contains("객실") && k.contains("가격")) { idxTypePrices = e.getValue(); break; }
                        }
                    }
                    if (log.isDebugEnabled()) {
                        int idxRoomTypes = col(headerMap, "객실 유형","객실타입","room type","room types");
                        log.debug("헤더 인덱스 → 가격:{}, 객실별가격:{}, 객실유형:{} / headerKeys={}",
                                idxPrice, idxTypePrices, idxRoomTypes, headerMap.keySet());
                    }
                    String typePricesText = getCellString(row, idxTypePrices);
                    Map<String,Integer> typePriceMap = parseTypeIntMap(typePricesText);

                    int commonPeople = getCellInt(row, col(headerMap, "수용 가능 인원", "정원", "인원", "people"));
                    if (commonPeople < 1) commonPeople = 2;

                    int commonCount = getCellInt(row, col(headerMap, "객실 수", "rooms", "객실수"));
                    if (commonCount < 1) commonCount = 1;

                    String roomTypesString = getCellString(row, col(headerMap, "객실 유형", "객실타입", "room type", "room types"));
                    if (roomTypesString.isEmpty()) roomTypesString = "기본 객실";
                    // ★ 분리 범위 확대: 줄바꿈/세미콜론/파이프/중점/대괄호 등
                    String[] roomTypeArray = roomTypesString.split("[,;/|·ㆍ・、\\n\\r()\\[\\]]+");

                    // 기존 룸 맵
                    List<Room> rooms = hotel.getRooms();
                    if (rooms == null) { rooms = new ArrayList<>(); hotel.setRooms(rooms); }
                    Map<String, Room> existingRoomsMap = rooms.stream()
                            .collect(Collectors.toMap(Room::getType, Function.identity(), (a, b) -> a));

                    for (String roomType : roomTypeArray) {
                        String finalRoomType = roomType.trim();
                        if (finalRoomType.isEmpty()) continue;

                        Room room = existingRoomsMap.get(finalRoomType);
                        if (room == null) {
                            room = new Room();
                            room.setHotel(hotel);
                            room.setType(finalRoomType);
                            rooms.add(room);
                        }

                        // 타입별 가격 우선, 없으면 공통가
                        Integer typePrice = lookupTypePrice(typePriceMap, finalRoomType);
                        int priceForThis = (typePrice != null && typePrice > 0) ? typePrice : commonPrice;
                        if (priceForThis > 0) {
                            room.setPrice(priceForThis);
                        }
                        if (log.isDebugEnabled()) {
                            log.debug("[{}] roomType='{}' → price={} (map={}, common={})",
                                    hotelName, finalRoomType, room.getPrice(), typePriceMap, commonPrice);
                        }

                        room.setPeople(commonPeople);
                        room.setCount(commonCount);
                        room.setCheckinTime(checkinTime);
                        room.setCheckoutTime(checkoutTime);
                        roomRepository.save(room);

                        // ───────── 객실 이미지 매핑 (파일시스템 → 웹경로) ─────────
                        String dynamicImageDirectoryPath = imageBaseDirectoryPath + hotel.getType() + "/";
                        String roomImageDirectoryPath = dynamicImageDirectoryPath + "rooms/";
                        File roomImageDir = new File(roomImageDirectoryPath);
                        Integer roomId = room.getRId();

                        if (roomId != null && roomImageDir.exists() && roomImageDir.isDirectory()) {
                            String roomIdPrefix = roomId.toString();
                            File[] matchingRoomFiles = roomImageDir.listFiles((dir, name) ->
                                    name.startsWith(roomIdPrefix + ".") || name.startsWith(roomIdPrefix + "_")
                            );

                            if (matchingRoomFiles != null) {
                                for (File roomImageFile : matchingRoomFiles) {
                                    String filename = roomImageFile.getName();
                                    boolean imageExists = (room.getImages() != null) && room.getImages().stream()
                                            .anyMatch(img -> img.getFilename() != null && img.getFilename().equals(filename));

                                    if (!imageExists) {
                                        HotelImage image = new HotelImage();
                                        image.setFilename(filename);
                                        image.setImageUrl("/images/" + hotel.getType() + "/rooms/" + filename);
                                        image.setImageType("room");

                                        image.setRoom(room);
                                        if (room.getImages() == null) {
                                            room.setImages(new ArrayList<>());
                                        }
                                        room.getImages().add(image);

                                        hotelImageRepository.save(image);
                                    }
                                }
                            }
                        }
                    }

                    // ───────── 호텔 메인/서브 이미지 매핑 ─────────
                    Long hotelId = hotel.getHId();
                    String dynamicImageDirectoryPath = imageBaseDirectoryPath + hotel.getType() + "/";
                    File imageDir = new File(dynamicImageDirectoryPath);
                    if (hotel.getImages() == null) hotel.setImages(new ArrayList<>());

                    if (hotelId != null && imageDir.exists() && imageDir.isDirectory()) {
                        String idPrefix = hotelId.toString();
                        File[] matchingFiles = imageDir.listFiles((dir, name) ->
                                name.startsWith(idPrefix + ".") || name.startsWith(idPrefix + "_")
                        );

                        if (matchingFiles != null) {
                            for (File imageFile : matchingFiles) {
                                String filename = imageFile.getName();
                                boolean imageExists = hotel.getImages().stream()
                                        .anyMatch(img -> img.getFilename() != null && img.getFilename().equals(filename));

                                if (!imageExists) {
                                    HotelImage image = new HotelImage();
                                    image.setFilename(filename);
                                    image.setImageUrl("/images/" + hotel.getType() + "/" + filename);
                                    image.setImageType(filename.contains("_") ? "sub" : "main");

                                    image.setHotel(hotel);
                                    hotel.getImages().add(image);

                                    hotelImageRepository.save(image);
                                }
                            }
                        }
                    }

                    // (선택) 호텔 최저가 갱신(필드가 있을 때 주석 해제)
                    try {
                        int min = hotel.getRooms().stream()
                                .filter(r -> r.getPrice() != null && r.getPrice() > 0)
                                .mapToInt(Room::getPrice).min().orElse(0);
                        // hotel.setMinPrice(min);
                    } catch (Exception ignore) {}

                    hotelRepository.save(hotel);
                }
            }
        }
    }

    // ───────────────────── 기타 유틸 ─────────────────────

    private String parseFirstTime(String text, String defaultValue) {
        if (text == null || text.isEmpty()) {
            return defaultValue;
        }
        Pattern pattern = Pattern.compile("\\d{2}:\\d{2}");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group();
        }

        return defaultValue;
    }

    private ServiceEntity findOrCreateService(String serviceName) {
        return serviceRepository.findByServiceName(serviceName)
                .orElseGet(() -> {
                    ServiceEntity newService = new ServiceEntity();
                    newService.setServiceName(serviceName);
                    return serviceRepository.save(newService);
                });
    }

    private String getCellString(Row row, int index) {
        if (row == null || index < 0) return "";
        Cell cell = row.getCell(index);
        return (cell == null) ? "" : cell.toString().trim();
    }

    private Integer getCellInt(Row row, int index) {
        if (row == null || index < 0) return 0;
        Cell cell = row.getCell(index);
        if (cell == null || cell.toString().trim().isEmpty()) return 0;
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        } else {
            try {
                String numStr = cell.toString().replaceAll("[^0-9.]", "");
                if (numStr.isEmpty()) return 0;
                if (numStr.contains(".")) {
                    numStr = numStr.substring(0, numStr.indexOf('.'));
                }
                return Integer.parseInt(numStr);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }

    private Double getCellDouble(Row row, int index) {
        if (row == null || index < 0) return null;
        Cell cell = row.getCell(index);
        if (cell == null || cell.toString().trim().isEmpty()) return null;
        if (cell.getCellType() == CellType.NUMERIC) return cell.getNumericCellValue();
        try {
            return Double.parseDouble(cell.toString().trim());
        } catch (Exception e) {
            return null;
        }
    }
}
