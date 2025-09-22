package com.example.backend.manager;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.search.Hotel;
import com.example.backend.search.HotelDTO;
import com.example.backend.search.HotelRepository;

import jakarta.transaction.Transactional;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelPicRepository hotelPicRepository;
    private final FileStorageService fileStorageService;

    public HotelService(HotelRepository hotelRepository, HotelPicRepository hotelPicRepository,
            FileStorageService fileStorageService) {
        this.hotelRepository = hotelRepository;
        this.hotelPicRepository = hotelPicRepository;
        this.fileStorageService = fileStorageService;
    }

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

        // 3. Hotel 저장
        hotel = hotelRepository.save(hotel);

        // 4. 이미지 처리
        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile file = images.get(i);

                // 👉 fileStorageService에서 경로와 파일명 따로 리턴하도록 설계
                FileStorageResult result = fileStorageService.saveHotelImage(hotel.getHId(), file);

                HotelPic pic = new HotelPic();
                pic.setHotel(hotel);
                pic.setDir(result.getDir()); // 경로만
                pic.setFileName(result.getFileName()); // 파일명만
                pic.setMainImageFlag(i == 0);

                hotelPicRepository.save(pic);
            }
        }

        return hotel;
    }
}