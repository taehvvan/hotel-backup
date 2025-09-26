package com.example.backend.reservation;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.login.security.PrincipalDetails;
import com.example.backend.register.UserEntity;
import com.example.backend.register.UserRepository;
import com.example.backend.search.RoomAvailabilityService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final RoomAvailabilityService roomAvailabilityService;
    // private final RestTemplate restTemplate; // Toss API 연동을 위한 HTTP 클라이언트

    @Transactional
    public PaymentResponseDTO completePayment(PaymentRequest request, PrincipalDetails principalDetails) {

        // 1. 예약 정보 조회 및 유효성 검증
        Reservation reservation = reservationRepository.findById(request.getReId())
                .orElseThrow(() -> new RuntimeException("결제할 예약 정보를 찾을 수 없습니다. ID: " + request.getReId()));

        UserEntity user = null;
        // 2-1. 인증된 사용자 정보가 있으면 최우선으로 사용 (가장 안전)
        if (principalDetails != null) {
            user = principalDetails.getUser();
        } 
        // 2-2. 인증 정보는 없지만 DTO에 userId가 있으면, 해당 ID로 사용자를 조회 (비회원/토큰 문제 대비)
        else if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElse(null); // 사용자를 못찾으면 null
        }
        
        // 2. ❗[핵심]❗ 토스페이먼츠 결제 승인 API 호출
        // 실제 운영 환경에서는 이 로직을 반드시 구현해야 합니다.
        // 여기서는 성공했다고 가정하고 시뮬레이션합니다.
        // verifyPaymentWithToss(request.getPaymentKey(), request.getOrderId(), request.getAmount());

        // 3. 객실 수 차감
        LocalDate checkin = reservation.getCheckin();
        LocalDate checkout = reservation.getCheckout();
        int quantity = reservation.getPeople(); // 예시: 예약 인원 기준 차감
        roomAvailabilityService.reserveRoom(reservation.getRoom().getRId(), checkin, checkout, quantity);

        // 4. 예약 상태 업데이트
        reservation.setStatus("예약 완료");
        reservationRepository.save(reservation);

        // 5. 결제 정보 생성
        Payment payment = Payment.builder()
                .user(user)
                .reservation(reservation)
                .room(reservation.getRoom())
                .payMethod(request.getPayMethod())
                .phone(request.getPhone())
                .paymentKey(request.getPaymentKey())
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        System.out.println("결제 완료 & 예약 상태 변경 완료 - pId: " + savedPayment.getPId());

        return new PaymentResponseDTO(savedPayment);
    }
    
    /**
     * 실제 토스페이먼츠 서버에 결제 승인을 요청하는 메서드 (구현 필요)
     */
    public void verifyPaymentWithToss(String paymentKey, String orderId, Long amount) {
        // 1. RestTemplate 또는 WebClient를 사용하여 토스 API 호출
        // 2. 요청 URL: https://api.tosspayments.com/v1/payments/confirm
        // 3. 필요한 Header (Authorization, Content-Type) 및 Body (paymentKey, orderId, amount) 구성
        // 4. 응답을 받아 결제 상태가 "DONE"인지, 금액이 일치하는지 확인
        // 5. 유효하지 않은 경우 Exception 발생
        System.out.println("Toss Payments 승인 API 호출 시뮬레이션 - 성공");
    }

    public ReservationResponseDTO findReservationForGuest(Integer reId, String phone) {
        return paymentRepository.findByReservation_ReIdAndPhone(reId, phone)
                .map(payment -> new ReservationResponseDTO(payment.getReservation()))
                .orElse(null);
    }
}
