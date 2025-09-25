package com.example.backend.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 관리자 페이지의 호텔 목록에 보여줄 데이터를 담는 DTO입니다.
 */
@Data
@AllArgsConstructor
public class AdminHotelDto {
    private Long hId;
    private String hName;
    private String region;
    private boolean active;
    private Long monthlyRevenue;
    private Long bookingCount;
    private Double averageRating;
}