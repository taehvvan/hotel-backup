package com.example.backend.reservation;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class PaymentRequest {
    @JsonProperty("userId")
    private Integer uId;

    @JsonProperty("roomId")
    private Integer rId;

    @JsonProperty("reservationId")
    private Integer reId;
    private String payMethod; // 예: "TOSS_PAY"
}
