package com.example.backend.search;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 사용자 ID로 리뷰 목록을 찾는 메소드
    List<Review> findByUserId(Integer userId);
}