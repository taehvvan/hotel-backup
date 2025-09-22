package com.example.backend.reservation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public Payment completePayment(Integer uId, Integer rId, Integer reId, String payMethod) {


        // 1. 결제 정보 저장
        Payment payment = new Payment();
        payment.setUId(uId);
        payment.setRId(rId);
        payment.setReId(reId);
        payment.setPayMethod(payMethod);

        if (uId == null) {
            System.out.println("uid null");
        } else if (rId == null) {
            System.out.println("rid null");
        } else if (reId == null) {
            System.out.println("reid null");
        }

        paymentRepository.save(payment);  // 결제 정보 저장

        // 2. 예약 상태 변경
        Reservation reservation = reservationRepository.findById(reId)
                .orElseThrow(() -> new RuntimeException("예약 정보가 없습니다."));
        reservation.setStatus("예약 완료");
        reservationRepository.save(reservation);

        return payment;
    }
}

