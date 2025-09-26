package com.example.backend.reservation;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class PaymentRequest {

    private Integer pId;

    // Reservation 정보
    private Integer reId;
    private Integer rId;
    private Integer hId;

    // User 정보 (비회원은 null)
    private Integer userId;
    private String phone;

    // Toss Payments 정보
    private String paymentKey;
    private String orderId;
    private Long amount;
    private String payMethod;
}
