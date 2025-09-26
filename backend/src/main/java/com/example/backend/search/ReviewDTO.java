package com.example.backend.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReviewDTO {
    private Long reviewId;
    private Integer reservationId; // [수정] Long -> Integer
    private Long hId;
    private String hotelName; 
    private String image;
    private Integer score;
    private String content;
    private String userName; 
    private LocalDateTime createdAt;

    public ReviewDTO(Review review) {
        this.reviewId = review.getReviewId();
        this.image = review.getImage();
        this.score = review.getScore();
        this.content = review.getContent();
        this.createdAt = review.getCreatedAt();

        if (review.getReservation() != null) {
            this.reservationId = review.getReservation().getReId();
        }

        if (review.getHotel() != null) {
            this.hId = review.getHotel().getHId();
            this.hotelName = review.getHotel().getHName();
        }
        
        if (review.getUser() != null) {
            this.userName = review.getUser().getName();
        }
    }
}