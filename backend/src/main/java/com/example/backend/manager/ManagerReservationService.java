package com.example.backend.manager;

import com.example.backend.register.UserEntity;
import com.example.backend.register.UserRepository;
import com.example.backend.reservation.Payment;
import com.example.backend.reservation.PaymentRepository;
import com.example.backend.reservation.Reservation;
import com.example.backend.reservation.ReservationRepository;
import com.example.backend.search.Hotel;
import com.example.backend.search.HotelImage;
import com.example.backend.search.HotelRepository; // HotelRepository import
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerReservationService {

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final HotelRepository hotelRepository; // UserRepository 대신 주입
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ManagerReservationDTO> getReservationsForManager(Integer ownerId) {
        // Repository에서 ownerId에 해당하는 모든 예약을 가져옵니다.
        List<Reservation> reservations = reservationRepository.findAllByHotelOwnerId(ownerId);
        
        // 각 Reservation 엔티티를 ManagerReservationDTO로 변환합니다.
        return reservations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // convertToDto 메서드는 이전과 동일합니다.
     private ManagerReservationDTO convertToDto(Reservation reservation) {
        
        // 1. Payment 정보를 Optional로 받아옵니다.
        Optional<Payment> optionalPayment = paymentRepository.findByReservation(reservation);

        // 2. 결제 정보 존재 여부에 따라 이름과 연락처를 설정합니다.
        String guestName;
        String phone;

        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            phone = payment.getPhone();
            // 회원이면 회원 이름을, 비회원이면 결제 시 사용된 전화번호를 이름으로 사용합니다.
            guestName = reservation.getUser() != null ? reservation.getUser().getName() : phone;
        } else {
            // 결제 정보가 없는 경우 (예: "예약 중" 상태)
            phone = "정보 없음";
            // 회원이면 회원 이름을, 비회원이면 "비회원"으로 표시합니다.
            guestName = reservation.getUser() != null ? reservation.getUser().getName() : "비회원";
        }

        String hotelImageUrl = Optional.ofNullable(reservation.getHotel())
                .flatMap(hotel -> hotel.getImages().stream()
                        .filter(img -> "main".equalsIgnoreCase(img.getImageType()))
                        .map(HotelImage::getImageUrl)
                        .findFirst()
                        .or(() -> hotel.getImages().stream().map(HotelImage::getImageUrl).findFirst()))
                .map(url -> {
                    // URL이 http로 시작하지 않으면, 서버 주소를 앞에 붙여줍니다.
                    if (url != null && !url.startsWith("http")) {
                        return "http://localhost:8888" + url;
                    }
                    return url; // 이미 완전한 URL이면 그대로 반환합니다.
                })
                .orElse(null);

        // 빌더 패턴을 사용하여 DTO를 생성합니다.
        return ManagerReservationDTO.builder()
                .reservationId(reservation.getReId())
                .orderId(reservation.getOrderId())
                .userId(reservation.getUser() != null ? reservation.getUser().getId() : null)
                .guestName(guestName)
                .phone(phone)
                .hotelName(reservation.getHotel().getHName())
                .roomType(reservation.getRoom().getType())
                .checkIn(reservation.getCheckin().toString())
                .checkOut(reservation.getCheckout().toString())
                .status(reservation.getStatus())
                .address(reservation.getHotel().getAddress())
                .hotelImage(hotelImageUrl)
                .build();
    }
}