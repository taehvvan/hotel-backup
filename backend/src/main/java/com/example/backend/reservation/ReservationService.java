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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final HotelRepository hotelRepository;

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
                .status("결제 대기") // "예약 중" -> "결제 대기"로 상태 명확화
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
}
