package com.example.backend.search;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class HotelExcelService {

    private final HotelRepository hotelRepository;
    private final ServiceRepository serviceRepository;
    private final RoomRepository roomRepository;
    private final HotelImageRepository hotelImageRepository;
    
    @Value("${file.upload-dir}")
    private String imageBaseDirectoryPath;

    public HotelExcelService(HotelRepository hotelRepository,
                             ServiceRepository serviceRepository,
                             RoomRepository roomRepository,
                             HotelImageRepository hotelImageRepository) {
        this.hotelRepository = hotelRepository;
        this.serviceRepository = serviceRepository;
        this.roomRepository = roomRepository;
        this.hotelImageRepository = hotelImageRepository;
    }

    @Transactional
    public void importHotelsFromExcel(String filePath, String hotelType) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);
        
        Map<String, Integer> headerMap = new HashMap<>();
        Row headerRow = sheet.getRow(0);
        if (headerRow != null) {
            for (Cell cell : headerRow) {
                if (cell != null && cell.getCellType() == CellType.STRING) {
                    headerMap.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
                }
            }
        }

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String hotelName = getCellString(row, headerMap.getOrDefault("명칭", 0));
            String hotelAddress = getCellString(row, headerMap.getOrDefault("주소", 4));

            Hotel hotel = hotelRepository.findByhNameAndAddress(hotelName, hotelAddress)
                    .orElseGet(() -> {
                        Hotel h = new Hotel();
                        h.setHName(hotelName);
                        if (hotelAddress != null && !hotelAddress.isEmpty()) {
                            String[] addressParts = hotelAddress.split(" ");
                            h.setRegion(addressParts.length >= 2 ? addressParts[0] + " " + addressParts[1] : addressParts[0]);
                        }
                        h.setAddress(hotelAddress);
                        h.setLatitude(getCellDouble(row, headerMap.getOrDefault("위도", 5)));
                        h.setLongitude(getCellDouble(row, headerMap.getOrDefault("경도", 6)));
                        h.setInfo(getCellString(row, headerMap.getOrDefault("개요", 7)));
                        h.setType(hotelType);
                        return hotelRepository.save(h);
                    });
            
            Set<ServiceEntity> serviceEntities = new HashSet<>();
            String detailInfo = getCellString(row, headerMap.getOrDefault("상세정보", -1));
            String wColumnText = getCellString(row, 22);
            if (!wColumnText.isEmpty()) {
                String[] facilities = wColumnText.split("[,/]");
                for (String facility : facilities) {
                    if (!facility.trim().isEmpty()) {
                        serviceEntities.add(findOrCreateService(facility.trim()));
                    }
                }
            }
            for (int colIdx = 20; colIdx <= 33; colIdx++) {
                if (colIdx == 22) continue;
                String headerName = getCellString(headerRow, colIdx);
                String cellValue = getCellString(row, colIdx);
                if (!headerName.isEmpty() && ("있음".equals(cellValue) || "가능".equals(cellValue) || "Y".equalsIgnoreCase(cellValue))) {
                    serviceEntities.add(findOrCreateService(headerName.trim()));
                }
            }
            if (!detailInfo.isEmpty()) {
                Pattern amenityPattern = Pattern.compile("([^\n\r:]+?)\\s*:\\s*Y");
                Matcher matcher = amenityPattern.matcher(detailInfo);
                while (matcher.find()) {
                    String amenityName = matcher.group(1).trim();
                    if (!amenityName.isEmpty()) {
                        serviceEntities.add(findOrCreateService(amenityName));
                    }
                }
            }
            if (!serviceEntities.isEmpty()) {
                hotel.setServices(new ArrayList<>(serviceEntities));
            }

            Map<String, Room> existingRoomsMap = hotel.getRooms().stream()
                .collect(Collectors.toMap(Room::getType, Function.identity()));

            String checkinTime = parseFirstTime(getCellString(row, 16), "18:00");
            String checkoutTime = parseFirstTime(getCellString(row, 17), "13:00");
            
            Integer commonPrice = 0;
            if (!detailInfo.isEmpty()) {
                Pattern pricePattern = Pattern.compile("비수기 주중최소.*?(\\d{1,3}(,\\d{3})*|\\d+)");
                Matcher priceMatcher = pricePattern.matcher(detailInfo);
                if (priceMatcher.find()) {
                    try {
                        String priceString = priceMatcher.group(1).replaceAll(",", "");
                        commonPrice = Integer.parseInt(priceString);
                    } catch (NumberFormatException e) { commonPrice = 0; }
                }
            }
            if (commonPrice == 0) {
                commonPrice = getCellInt(row, headerMap.getOrDefault("객실가격", 10));
            }
            
            Integer commonPeople = getCellInt(row, headerMap.getOrDefault("수용 가능 인원", 11));
            if (commonPeople < 1) commonPeople = 2;
            
            Integer commonCount = getCellInt(row, headerMap.getOrDefault("객실 수", 12));
            if (commonCount < 1) commonCount = 1;

            String roomTypesString = getCellString(row, headerMap.getOrDefault("객실타입", 9));
            if(roomTypesString.isEmpty()) { roomTypesString = getCellString(row, headerMap.getOrDefault("객실 유형", 13)); }
            if (roomTypesString.isEmpty()) { roomTypesString = "기본 객실"; }

            String[] roomTypeArray = roomTypesString.split("[,/·()]+");
            for (String roomType : roomTypeArray) {
                String finalRoomType = roomType.trim();
                if (finalRoomType.isEmpty()) continue;

                Room room = existingRoomsMap.get(finalRoomType);

                if (room != null) {
                    room.setPrice(commonPrice);
                    room.setPeople(commonPeople);
                    room.setCount(commonCount);
                    room.setCheckinTime(checkinTime);
                    room.setCheckoutTime(checkoutTime);
                } else {
                    room = new Room();
                    room.setHotel(hotel);
                    room.setType(finalRoomType);
                    room.setPrice(commonPrice);
                    room.setPeople(commonPeople);
                    room.setCount(commonCount);
                    room.setCheckinTime(checkinTime);
                    room.setCheckoutTime(checkoutTime);
                    hotel.getRooms().add(room);
                }
                
                roomRepository.save(room);

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
                                image.setImageUrl(roomImageFile.getPath().replace("\\", "/"));
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
            
            Long hotelId = hotel.getHId();
            String dynamicImageDirectoryPath = imageBaseDirectoryPath + hotel.getType() + "/";
            File imageDir = new File(dynamicImageDirectoryPath);

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
                            image.setImageUrl(imageFile.getPath().replace("\\", "/"));
                            image.setImageType(filename.contains("_") ? "sub" : "main");
                            
                            image.setHotel(hotel);
                            hotel.getImages().add(image);

                            hotelImageRepository.save(image);
                        }
                    }
                }
            }
            
            hotelRepository.save(hotel);
        }
        workbook.close();
        fis.close();
    }

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