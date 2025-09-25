package com.example.backend.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewDTO {
    private Long reviewId;
    private Long pId;
    private Long hId;
    private String image;
    private Integer score;
    private String content;

    public ReviewDTO(Review review) {
        this.reviewId = review.getReviewId();
        this.image = review.getImage();
        this.score = review.getScore();
        this.content = review.getContent();

        // ✅ [수정] Review 엔티티에 Payment와의 직접적인 관계가 없으므로 관련 코드를 제거하거나,
        // 올바른 연관 관계(예: review.getReservation().getPayment())로 수정해야 합니다.
        // 여기서는 컴파일을 위해 임시로 주석 처리합니다.
        /*
        if (review.getPayment() != null) {
            this.pId = review.getPayment().getPId();
        }
        */

        if (review.getHotel() != null) {
            this.hId = review.getHotel().getHId();
        }
    }
}
