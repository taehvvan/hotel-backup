package com.example.backend.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomAvailabilityService {

    private final RoomAvailabilityRepository roomAvailabilityRepository;
    private final RoomRepository roomRepository;

    // 예약 가능 여부만 체크 (true / false)
    public boolean isRoomAvailable(Integer rId, LocalDate checkin, LocalDate checkout, int quantity) {
        List<LocalDate> dates = checkin.datesUntil(checkout).toList();
        for (LocalDate date : dates) {
            RoomAvailability ra = roomAvailabilityRepository
                    .findByRoom_rIdAndDate(rId, date)
                    .orElseGet(() -> {
                        Room room = roomRepository.findById(rId)
                                .orElseThrow(() -> new RuntimeException("객실 정보 없음"));
                        return roomAvailabilityRepository.save(new RoomAvailability(null, room, date, room.getCount()));
                    });
            if (ra.getAvailableCount() < quantity) return false;
        }
        return true;
    }

    // 날짜별 객실 수 리스트 조회
    @Transactional(readOnly = true)
    public List<RoomAvailabilityDTO> getAvailabilities(Integer rId, LocalDate checkin, LocalDate checkout) {
        List<LocalDate> dates = checkin.datesUntil(checkout).toList();
        List<RoomAvailabilityDTO> result = new ArrayList<>();

        for (LocalDate date : dates) {
            RoomAvailability ra = roomAvailabilityRepository
                    .findByRoom_rIdAndDate(rId, date)
                    .orElseGet(() -> {
                        Room room = roomRepository.findById(rId)
                                .orElseThrow(() -> new RuntimeException("객실 정보 없음"));
                        return roomAvailabilityRepository.save(
                                new RoomAvailability(null, room, date, room.getCount())
                        );
                    });
            result.add(new RoomAvailabilityDTO(ra));
        }
        return result;
    }

    // 예약 후 객실 수 차감
    @Transactional
    public void reserveRoom(Integer rId, LocalDate checkin, LocalDate checkout, int quantity) {
        List<LocalDate> dates = checkin.datesUntil(checkout).toList();
        for (LocalDate date : dates) {
            RoomAvailability ra = roomAvailabilityRepository
                    .findByRoom_rIdAndDate(rId, date)
                    .orElseThrow(() -> new RuntimeException("객실 정보 없음"));
            int remaining = ra.getAvailableCount() - quantity;
            if (remaining < 0) throw new RuntimeException("잔여 객실 부족");
            ra.setAvailableCount(remaining);
            roomAvailabilityRepository.save(ra);
        }
    }
}