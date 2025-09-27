package com.example.backend.manager;

import com.example.backend.register.UserEntity;
import com.example.backend.register.UserRepository;
import com.example.backend.reservation.Reservation;
import com.example.backend.reservation.ReservationRepository;
import com.example.backend.search.Hotel;
import com.example.backend.search.HotelRepository; // HotelRepository import
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerReservationService {

    private final ReservationRepository reservationRepository;
    private final HotelRepository hotelRepository; // UserRepository 대신 주입
    private final UserRepository userRepository;

    public List<ManagerReservationDTO> getAllReservationsForManager(String businessNumber) {

        System.out.println("==================== 예약 조회 시작 ====================");
        System.out.println("[1] 전달받은 사업자 번호 : " + businessNumber);

        // 1. 사업자 번호로 UserEntity(매니저)를 찾습니다.
        UserEntity manager = userRepository.findByBusinessNumber(businessNumber)
                .orElseThrow(() -> new IllegalArgumentException("해당 사업자 번호의 매니저를 찾을 수 없습니다: " + businessNumber));

        System.out.println("[2] 사업자 번호로 찾은 매니저 ID : " + manager.getId());

        // 2. 찾은 매니저의 id(PK)를 사용하여 소유한 모든 호텔을 조회합니다.
        List<Hotel> managerHotels = hotelRepository.findByUser_Id(manager.getId());

        if (managerHotels.isEmpty()) {
            return List.of(); // 관리하는 호텔이 없으면 빈 리스트 반환
        }

        // 3. 호텔 ID 목록(hId)을 추출합니다.
        List<Long> hotelIds = managerHotels.stream()
                .map(Hotel::getHId)
                .collect(Collectors.toList());

        System.out.println("[3] 매니저가 소유한 호텔 ID 목록 : " + hotelIds);

        // 4. 호텔 ID 목록으로 모든 예약 정보를 조회합니다.
        List<Reservation> reservations = reservationRepository.findByHotel_hIdIn(hotelIds);

        System.out.println("[4] 호텔 ID 목록으로 찾은 예약 건수 : " + reservations.size());
        
        if (reservations.isEmpty()) {
            System.out.println("[4-1] 해당 호텔들에 대한 예약 내역이 없습니다.");
        }

        System.out.println("==================== 예약 조회 종료 ====================");

        // 5. DTO로 변환하여 반환합니다.
        return reservations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // convertToDto 메서드는 이전과 동일합니다.
    private ManagerReservationDTO convertToDto(Reservation reservation) {
        String guestName = (reservation.getUser() != null) ? reservation.getUser().getName() : "비회원";
        String hotelName = (reservation.getHotel() != null) ? reservation.getHotel().getHName() : "알 수 없는 호텔";
        String roomType = (reservation.getRoom() != null) ? reservation.getRoom().getType() : "알 수 없는 객실";

        String checkInDate = (reservation.getCheckin() != null) ? reservation.getCheckin().toString() : "";
        String checkOutDate = (reservation.getCheckout() != null) ? reservation.getCheckout().toString() : "";
        String status = (reservation.getStatus() != null) ? reservation.getStatus() : "상태 미지정";
        
        return ManagerReservationDTO.builder()
                .reservationId(reservation.getReId())
                .userName(guestName)
                .hotelName(hotelName)
                .roomType(roomType)
                .checkIn(checkInDate)
                .checkOut(checkOutDate)
                .status(status)
                .build();
    }
}