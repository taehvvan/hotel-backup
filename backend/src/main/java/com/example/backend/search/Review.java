package com.example.backend.search;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Review {

    @Id
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "p_id")
    private Long pId; // TODO: Payments 엔티티와 매핑 필요

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "h_id") // 호텔 외래키
    private Hotel hotel;

    @Column(name = "image")
    private String image; // DB 타입이 file → 문자열 경로 or BLOB 추천

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "content")
    private String content;
}