package com.example.backend.reservation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.register.UserEntity;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    // ✅ rId 파라미터를 제거하여 로직을 단순화합니다.
    public Payment completePayment(UserEntity user, Integer reId, String payMethod, String phone) {
        System.out.println("결제 완료 처리 시작 - reId: " + reId);

        if (reId == null) {
            throw new IllegalArgumentException("reId가 null입니다.");
        }
        
        // 1. 전달받은 reId로 Reservation 정보를 조회합니다.
        Reservation reservation = reservationRepository.findById(reId)
                .orElseThrow(() -> new RuntimeException("결제할 예약 정보를 찾을 수 없습니다. ID: " + reId));

        // 2. 결제 정보 생성
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setReservation(reservation); // Reservation 객체 설정
        payment.setRoom(reservation.getRoom()); // Reservation 객체에서 Room 정보를 가져와 설정
        payment.setPayMethod(payMethod);
        payment.setPhone(phone);

        paymentRepository.save(payment);

        // 3. 예약 상태를 "결제 대기"에서 "예약 완료"로 변경
        reservation.setStatus("예약 완료");
        reservationRepository.save(reservation);

        System.out.println("결제 및 예약 상태 변경 완료 - pId: " + payment.getPId());
        return payment;
    }

    public ReservationResponseDTO findReservationForGuest(Integer pId, String phone) {
        return paymentRepository.findBypIdAndPhone(pId, phone)
                .map(payment -> new ReservationResponseDTO(payment.getReservation()))
                .orElse(null);
    }
}
