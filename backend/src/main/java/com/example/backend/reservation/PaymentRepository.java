package com.example.backend.reservation;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    // 결제번호 + 전화번호로 조회
    Optional<Payment> findBypIdAndPhone(Integer pId, String phone);
}
