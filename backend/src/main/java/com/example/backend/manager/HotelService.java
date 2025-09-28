package com.example.backend.manager;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.reservation.Reservation;
import com.example.backend.reservation.ReservationRepository;
import com.example.backend.search.Hotel;
import com.example.backend.search.HotelDTO;
import com.example.backend.search.HotelImage;
import com.example.backend.search.HotelRepository;
import com.example.backend.search.HotelImageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelImageRepository hotelImageRepository;
    private final HotelPicRepository hotelPicRepository;
    private final FileStorageService fileStorageService;
    private final ReservationRepository reservationRepository;



    @Transactional
public Hotel saveHotel(HotelDTO dto, List<MultipartFile> images) {
    // 1. 기존 호텔 불러오기 or 새 호텔 생성
    Hotel hotel = dto.getHId() != null
            ? hotelRepository.findById(dto.getHId()).orElse(new Hotel())
            : new Hotel();

    // 2. 기본 정보 세팅
    hotel.setHName(dto.getHName());
    hotel.setType(dto.getType());
    hotel.setRegion(dto.getRegion());
    hotel.setAddress(dto.getAddress());
    hotel.setStar(dto.getStar());

    // 3. Hotel 저장 (ID를 먼저 생성하거나 기존 객체를 가져오기 위함)
    hotel = hotelRepository.save(hotel);

    // 4. 이미지 처리
    if (images != null && !images.isEmpty()) {
        for (int i = 0; i < images.size(); i++) {
            MultipartFile file = images.get(i);
            if(file.isEmpty()) continue;

            // ★★★★★ 수정된 부분: hotel.getType()을 추가하여 호출 ★★★★★
            FileStorageResult result = fileStorageService.saveHotelImage(hotel.getHId(), hotel.getType(), file, i);

            // HotelPic 엔티티 대신 HotelImage 엔티티를 사용한다고 가정하고 수정합니다.
            // 만약 HotelPic이 맞다면, new HotelPic()으로 사용하시면 됩니다.
            HotelImage pic = new HotelImage();
            pic.setHotel(hotel);
            
            // FileStorageResult 대신 웹 URL 경로를 직접 저장하는 방식으로 변경된 것을 반영합니다.
            String imageUrl = "/images/" + hotel.getType() + "/" + hotel.getHId() + "/" + result.getFileName();
            pic.setImageUrl(imageUrl); // 예시: /images/Hotel/1/uuid_name.jpg
            pic.setFilename(file.getOriginalFilename());
            pic.setImageType(file.getContentType());
            
            // pic.setMainImageFlag(i == 0); // mainImageFlag 필드가 HotelImage에 있다면 이 코드 사용

            hotelImageRepository.save(pic); // HotelImageRepository 사용
        }
    }

    return hotel;
}
}