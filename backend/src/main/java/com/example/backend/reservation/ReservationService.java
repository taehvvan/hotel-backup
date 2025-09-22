package com.example.backend.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.register.UserEntity;
import com.example.backend.register.UserRepository;
import com.example.backend.search.Room;
import com.example.backend.search.RoomRepository;
import com.example.backend.reservation.ReservationResponseDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public Reservation createReservation(ReservationRequest request) {

        // 1. 객실 조회
        if (request.getRId() == null) {
            throw new IllegalArgumentException("객실 ID가 필요합니다.");
        }

        Room room = roomRepository.findById(request.getRId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 객실입니다."));

        // 2. 유저 조회 (비회원이면 null)
        UserEntity user = null;
        if (request.getUId() != null) {
            user = userRepository.findById(request.getUId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        }

        // 3. 예약 생성
        Reservation reservation = Reservation.builder()
                .room(room)
                .user(user) // 비회원이면 null 가능
                .checkin(request.getCheckin())
                .checkout(request.getCheckout())
                .people(request.getPeople())
                .price(request.getPrice())
                .status("예약 중")
                .createdAt(LocalDateTime.now())
                .build();

        // 4. 저장 후 반환
        return reservationRepository.save(reservation);
    }

    public List<ReservationResponseDTO> findMyReservations(Integer userId) {

        List<Reservation> rawResults = reservationRepository.findRawReservationsByUserIdForDebug(userId);
        System.out.println("[DEBUG-SERVICE] Raw 쿼리 결과: 총 {}개의 Reservation 엔티티를 찾았습니다 : " + rawResults.size());

        if (rawResults.isEmpty()) {
            System.out.println("!!!!! 근본 원인 발견: Raw 쿼리조차 데이터를 찾지 못했습니다.");
            System.out.println("!!!!! 확인 사항: Reservation.java의 'private UserEntity user' 필드와 @ManyToOne, @JoinColumn(name=\"u_id\") 어노테이션이 올바르게 설정되었는지 확인해주세요.");
        } else {
            System.out.println("✅ Raw 쿼리는 성공! 문제는 DTO 프로젝션 또는 다른 JOIN에 있습니다.");
        }
        
        List<ReservationResponseDTO> results = reservationRepository.findMyReservationsByUserId(userId);
        
        // [★★★★★ 디버깅 포인트 ★★★★★]
        // 쿼리 실행 후 실제로 몇 개의 데이터를 찾았는지 확인합니다.
        System.out.println("[BACKEND-SERVICE] 조회 결과: 총 {}개의 예약 내역을 찾았습니다 : " + results.size());

        return results;
    }
}
