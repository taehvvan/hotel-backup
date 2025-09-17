package com.example.backend.search;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelDTO {
    private Long hId;
    private String hName;
    private String type;
    private String region;
    private String address;
    private Integer star;
    private Boolean active;
    private String info;

    private List<RoomDTO> rooms;
    private List<ServiceDTO> services; // <-- DTO로 변경

    private Double avgScore; // 평균 점수
    private Integer reviewCount; // 리뷰 개수

    private Integer minPrice; // <-- 최저가 추가
}
