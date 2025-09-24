package com.example.backend.reservation;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class PaymentRequest {

    private Integer pId;

    private Integer rId;

    private Integer reId;

    private String payMethod; // 예: "TOSS_PAY"

    private String phone;
}
