package com.example.backend.manager;

import com.example.backend.register.UserEntity;
import com.example.backend.search.Hotel;
import com.example.backend.search.HotelImage;
import com.example.backend.search.HotelImageRepository;
import com.example.backend.search.HotelRepository;
import com.example.backend.search.Room;
import com.example.backend.search.RoomRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerHotelService {

    private final HotelRepository hotelRepository;
    private final FileStorageService fileStorageService;
    private final HotelImageRepository hotelImageRepository;
    private final RoomRepository roomRepository;

    public List<ManagerHotelDTO> getHotelsByManager(Integer managerId) {
        // 1. 매니저 ID로 소유한 모든 호텔을 조회합니다.
        //    (UserEntity의 PK가 'id'이므로 findByUser_Id 사용)
        List<Hotel> hotels = hotelRepository.findByUser_Id(managerId);

        // 2. 조회된 호텔 목록을 DTO로 변환하여 반환합니다.
        return hotels.stream()
                     .map(this::convertToDto)
                     .collect(Collectors.toList());
    }

    @Transactional
    public ManagerHotelDTO createHotel(HotelSaveRequestDto hotelDto, List<MultipartFile> images, UserEntity manager) {
        // 1. Hotel 엔티티 생성 및 기본 정보 설정
        Hotel hotel = new Hotel();
        hotel.setHName(hotelDto.getName());
        hotel.setAddress(hotelDto.getLocation());
        hotel.setType(hotelDto.getType());
        hotel.setStar(hotelDto.getStars());
        hotel.setUser(manager);
        hotel.setLatitude(hotelDto.getLatitude());
        hotel.setLongitude(hotelDto.getLongitude());
        if (hotelDto.getLocation() != null && !hotelDto.getLocation().isEmpty()) {
            hotel.setRegion(hotelDto.getLocation().split(" ")[0]);
        }
        
        // ★★★★★ 2. Room 엔티티를 생성하여 Hotel의 자식으로 추가 ★★★★★
        if (hotelDto.getRooms() != null && !hotelDto.getRooms().isEmpty()) {
            for (RoomSaveRequestDTO roomDto : hotelDto.getRooms()) {
                Room room = new Room();
                room.setHotel(hotel); // 부모-자식 관계 설정
                room.setType(roomDto.getType());
                room.setPrice(roomDto.getPrice());
                room.setCount(roomDto.getCount());
                room.setPeople(roomDto.getPeople());
                room.setCheckinTime(roomDto.getCheckinTime());
                room.setCheckoutTime(roomDto.getCheckoutTime());
                room.setInfo(null);
                
                // 생성된 Room을 Hotel의 rooms 리스트에 추가
                hotel.getRooms().add(room);
            }
        }

        // ★★★★★ 3. Hotel을 저장 (Room도 함께 저장됨 - Cascade) ★★★★★
        Hotel savedHotel = hotelRepository.save(hotel);

        // 4. 이미지 파일 저장 및 HotelImage 엔티티 생성 (기존 로직)
        // (이 로직은 Hotel ID가 필요하므로 Hotel 저장 이후에 실행)
        List<HotelImage> hotelImages = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile imageFile = images.get(i);
                if (imageFile.isEmpty()) continue;

                FileStorageResult result = fileStorageService.saveHotelImage(savedHotel.getHId(), savedHotel.getType(), imageFile, i);
                String imageUrl = "/images/" + savedHotel.getType() + "/" + result.getFileName();

                HotelImage hotelImage = new HotelImage();
                hotelImage.setHotel(savedHotel);
                hotelImage.setFilename(result.getFileName());
                hotelImage.setImageType(i == 0 ? "main" : "sub");
                hotelImage.setImageUrl(imageUrl);
                hotelImages.add(hotelImage);
            }
            hotelImageRepository.saveAll(hotelImages);
        }
        savedHotel.setImages(hotelImages);
        
        return convertToDto(savedHotel);
    }

    @Transactional(readOnly = true)
    public ManagerHotelDTO getHotelDetails(Long hotelId, UserEntity manager) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("해당 숙소를 찾을 수 없습니다. ID: " + hotelId));

        if (!hotel.getUser().getId().equals(manager.getId())) {
            throw new SecurityException("이 숙소 정보를 조회할 권한이 없습니다.");
        }
        
        // 1. 기본 정보를 DTO로 변환합니다. (이때 rooms는 포함되지 않음)
        ManagerHotelDTO hotelDto = convertToDto(hotel);
        
        // 2. 변환된 DTO에 지연 로딩된 객실 정보를 직접 설정합니다.
        //    @Transactional 범위 안에서 호출되므로 안전합니다.
        hotelDto.setRooms(hotel.getRooms());
        
        return hotelDto;
    }

    @Transactional
    public ManagerHotelDTO updateHotel(Long hotelId, HotelSaveRequestDto hotelDto, UserEntity manager) {
        Hotel existingHotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("해당 숙소를 찾을 수 없습니다. ID: " + hotelId));

        if (!existingHotel.getUser().getId().equals(manager.getId())) {
            throw new SecurityException("이 숙소를 수정할 권한이 없습니다.");
        }
        
        // DTO의 정보로 기존 엔티티의 값을 업데이트
        existingHotel.setHName(hotelDto.getName());
        existingHotel.setAddress(hotelDto.getLocation());
        // ... (HotelSaveRequestDto에 정의된 다른 필드들도 업데이트) ...
        
        // @Transactional에 의해 변경된 내용은 자동 저장됩니다.
        return convertToDto(existingHotel);
    }

    @Transactional
    public void addImagesToHotel(Long hotelId, List<MultipartFile> images, UserEntity manager) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("해당 숙소를 찾을 수 없습니다. ID: " + hotelId));
        // ... (권한 확인) ...
        
        hotelImageRepository.deleteAll(hotel.getImages());
        hotel.getImages().clear();

        List<HotelImage> newHotelImages = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile imageFile = images.get(i);
                if(imageFile.isEmpty()) continue;

                // ★★★★★ 수정: imageIndex(i) 전달 ★★★★★
                FileStorageResult result = fileStorageService.saveHotelImage(hotel.getHId(), hotel.getType(), imageFile, i);
                String imageUrl = "/images/" + hotel.getType() + "/" + result.getFileName();
                
                HotelImage hotelImage = new HotelImage();
                hotelImage.setHotel(hotel);
                // ★★★★★ 수정: filename에 원본 대신 새로 생성된 파일명 저장 ★★★★★
                hotelImage.setFilename(result.getFileName());
                hotelImage.setImageType(i == 0 ? "main" : "sub");
                hotelImage.setImageUrl(imageUrl);
                
                newHotelImages.add(hotelImage);
            }
            hotelImageRepository.saveAll(newHotelImages);
            hotel.getImages().addAll(newHotelImages);
        }
    }

    @Transactional
    public void deleteHotel(Long hotelId, UserEntity manager) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("해당 숙소를 찾을 수 없습니다. ID: " + hotelId));

        if (!hotel.getUser().getId().equals(manager.getId())) {
            throw new SecurityException("이 숙소를 삭제할 권한이 없습니다.");
        }
        
        // 연관된 이미지 파일 물리적 삭제 (선택적)
        // ... fileStorageService.deleteDirectory(...) ...
        
        hotelRepository.delete(hotel);
    }

    private ManagerHotelDTO convertToDto(Hotel hotel) {
        // 전체 이미지 URL 리스트 생성
        List<String> imageUrls = hotel.getImages() != null ? hotel.getImages().stream()
                .map(HotelImage::getImageUrl)
                .map(url -> {
                    // ★★★★★ 수정된 URL 변환 로직 ★★★★★
                    // DB에 저장된 경로가 절대 경로든 상대 경로든, 웹 접근 URL로 변환합니다.
                    if (url != null && url.startsWith("D:")) {
                        // 예: "D:/hotel_images/Hotel/1.jpg" -> "/images/Hotel/1.jpg"
                        String relativePath = url.substring(url.indexOf("hotel_images") + "hotel_images".length()).replace("\\", "/");
                        return "http://localhost:8888/images" + relativePath;
                    } else if (url != null && url.startsWith("/images")) {
                        // 예: "/images/Hotel/1.jpg" -> "http://localhost:8888/images/Hotel/1.jpg"
                        return "http://localhost:8888" + url;
                    }
                    return url; // 그 외의 경우는 그대로 반환
                })
                .collect(Collectors.toList()) : new ArrayList<>();

        // ★★★★★ 대표 이미지 URL 설정 (첫 번째 이미지가 없으면 null) ★★★★★
        String mainImageUrl = !imageUrls.isEmpty() ? imageUrls.get(0) : null;

        return ManagerHotelDTO.builder()
                .id(hotel.getHId())
                .name(hotel.getHName())
                .location(hotel.getAddress())
                .type(hotel.getType())
                .stars(hotel.getStar())
                .latitude(hotel.getLatitude())
                .longitude(hotel.getLongitude())
                // .amenities( ... ) // amenities 정보 추가 필요 시 구현
                .images(imageUrls)
                .image(mainImageUrl)
                .rooms(hotel.getRooms()) // ★★★★★ 호텔 엔티티에서 직접 객실 리스트를 가져와 설정 ★★★★★
                .build();
    }
}