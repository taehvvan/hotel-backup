package com.example.backend.reservation;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReservationRequest {
    private Integer rId;         // 객실 ID
    private Integer uId;         // 회원 ID (비회원이면 null)
    private Long hId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkin;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkout;
    
    private int people;
    private int price;

}

