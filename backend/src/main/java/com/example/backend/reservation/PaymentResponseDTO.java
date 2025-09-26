package com.example.backend.reservation;

import lombok.Getter;

@Getter
public class PaymentResponseDTO {
    private final Integer paymentId;
    private final Integer reservationId;
    private final String orderId;
    private final Long amount;

    // Payment 엔티티를 받아서 DTO를 생성하는 생성자
    public PaymentResponseDTO(Payment payment) {
        this.paymentId = payment.getPId();
        this.reservationId = payment.getReservation().getReId();
        this.orderId = payment.getOrderId();
        this.amount = payment.getAmount();
    }
}
