package com.example.backend.reservation;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.register.UserEntity;
import com.example.backend.register.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public Payment completePayment(UserEntity user, Integer rId, Integer reId, String payMethod, String phone) {
        System.out.println("디버깅 - rId: " + rId + ", reId: " + reId); // 로그 추가

        if (rId == null) throw new IllegalArgumentException("rId가 null입니다.");
        if (reId == null) throw new IllegalArgumentException("reId가 null입니다.");


        
        // 결제 정보 생성
        Payment payment = new Payment();
        payment.setUser(user); // 회원이면 UserEntity, 비회원이면 null
        payment.setRId(rId);
        payment.setReId(reId);
        payment.setPayMethod(payMethod);
        payment.setPhone(phone);

        paymentRepository.save(payment);

        // 예약 상태 변경
        Reservation reservation = reservationRepository.findById(reId)
                .orElseThrow(() -> new RuntimeException("예약 정보를 찾을 수 없습니다."));
        reservation.setStatus("예약 완료");
        reservationRepository.save(reservation);

        return payment;
    }

    // 비회원 예약 확인
    public ReservationResponseDTO findReservationForGuest(Integer pId, String phone) {
        return paymentRepository.findBypIdAndPhone(pId, phone)
                .map(payment -> reservationRepository.findById(payment.getReId())
                        .map(ReservationResponseDTO::new)
                        .orElse(null))
                .orElse(null);
    }
}
