package com.example.backend.search;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDTO {
    private Long reviewId;
    private Long pId; // TODO: Payments 엔티티와 매핑 필요
    private Long hId;
    private String image; // DB 타입이 file → 문자열 경로 or BLOB 추천
    private Integer score;
    private String content;
}
