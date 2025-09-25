package com.example.backend.admin;

import com.example.backend.search.Hotel;
import com.example.backend.search.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 관리자의 호텔 관리 기능에 대한 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminHotelService {

    private final HotelRepository hotelRepository;

    public List<AdminHotelDto> getHotels(String type) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        return hotelRepository.findHotelsForAdminByType(type, oneMonthAgo);
    }

    @Transactional
    public void updateHotelStatus(Long hotelId, boolean active) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 호텔입니다. ID: " + hotelId));
        
        hotel.setActive(active);
        hotelRepository.save(hotel);
    }
}