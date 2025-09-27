package com.example.backend.reservation;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.register.UserEntity;
import com.example.backend.register.UserRepository;
import com.example.backend.search.Hotel;
import com.example.backend.search.HotelRepository;
import com.example.backend.search.Room;
import com.example.backend.search.RoomRepository;
import com.example.backend.search.RoomAvailabilityService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final HotelRepository hotelRepository;
    private final RoomAvailabilityService roomAvailabilityService;

    @Transactional
    public ReservationPrepareResponse createReservation(ReservationRequest request) {
        // 1. 객실 조회
        Room room = roomRepository.findById(request.getRId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 객실입니다. ID: " + request.getRId()));

                Hotel hotel = hotelRepository.findById(request.getHId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 호텔입니다. ID: " + request.getHId()));

        // 2. 유저 조회 (비회원인 경우 null)
        UserEntity user = null;
        if (request.getUId() != null) {
            user = userRepository.findById(request.getUId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. ID: " + request.getUId()));
        }

        // 3. 예약 생성
        Reservation reservation = Reservation.builder()
                .room(room)
                .user(user)
                .hotel(hotel)
                .checkin(request.getCheckin())
                .checkout(request.getCheckout())
                .people(request.getPeople())
                .price(request.getPrice())
                .status("예약 중")
                .createdAt(LocalDateTime.now())
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        // 4. 프론트엔드에 필요한 모든 정보를 담은 DTO를 반환
        return new ReservationPrepareResponse(savedReservation);
    }

    public List<ReservationResponseDTO> findMyReservations(Integer userId) {
        return reservationRepository.findMyReservationsByUserId(userId);
    }

    public ReservationResponseDTO findGuestReservation(Integer reId, String phone) {
        Payment payment = paymentRepository.findByReservation_ReIdAndPhone(reId, phone).orElse(null);
        if (payment == null || payment.getReservation() == null) {
            return null;
        }
        return new ReservationResponseDTO(payment.getReservation());
    }

    public Reservation findReservationById(Integer reservationId) {
        return reservationRepository.findById(reservationId).orElse(null);
    }

    @Transactional
    public void cancelReservation(Integer reservationId) {
        
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("해당 예약이 존재하지 않습니다."));
        
        // 1. 예약 상태를 '예약 취소'로 변경
        reservation.setStatus("예약 취소");
        reservationRepository.save(reservation);

        // ✅ 2. [핵심 로직 추가] 예약했던 날짜들의 객실 재고를 다시 1씩 늘려줍니다.
        // 일반적으로 한 번에 하나의 객실을 예약/취소하므로 quantity는 1입니다.
        roomAvailabilityService.cancelRoomReservation(
            reservation.getRoom().getRId(),
            reservation.getCheckin(),
            reservation.getCheckout(),
            1 // 1개의 객실을 복원
        );
    }

    

    @Transactional
    public void markAsDeleted(Integer reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예약이 존재하지 않습니다."));
        if (!"예약 취소".equals(reservation.getStatus())) {
            throw new RuntimeException("취소된 예약만 삭제할 수 있습니다.");
        }
        reservation.setStatus("삭제됨");
        reservationRepository.save(reservation);
    }
}
