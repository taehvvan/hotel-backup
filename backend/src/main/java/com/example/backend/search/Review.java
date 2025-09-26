package com.example.backend.search;

import com.example.backend.register.UserEntity;
import com.example.backend.reservation.Reservation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Review {

    @Id
    // [핵심 수정] 이 어노테이션이 ID를 자동으로 생성해주는 역할을 합니다.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "h_id")
    private Hotel hotel;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "re_id", unique = true)
    private Reservation reservation;

    @Column(name = "image")
    private String image;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}