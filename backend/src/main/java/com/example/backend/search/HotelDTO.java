package com.example.backend.search;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelDTO {

    @JsonProperty("hId")
    private Long hId;

    @JsonProperty("hName")
    private String hName;

    private String type;
    private String region;
    private String address;
    private Integer star;
    private Boolean active;
    private String info;

    private List<RoomDTO> rooms;
    private List<ReviewDTO> reviews;
    private List<ServiceDTO> services; // <-- DTO로 변경
    private List<HotelImageDTO> images;

    private Double avgScore; // 평균 점수
    private Integer reviewCount; // 리뷰 개수

    private Integer minPrice; // <-- 최저가 추가
}
