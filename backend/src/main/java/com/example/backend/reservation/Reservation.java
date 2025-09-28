package com.example.backend.reservation;

import java.security.SecureRandom;
import java.util.Base64;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.example.backend.register.UserEntity;
import com.example.backend.search.Hotel;
import com.example.backend.search.Room;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "re_id")
    private Integer reId;

    @Column(name = "order_id", unique = true, nullable = false)
    private String orderId;

    @ManyToOne
    @JoinColumn(name = "r_id")   // 객실 ID FK
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "h_id", nullable = false) // 호텔 ID FK
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id", nullable = true) // nullable 허용
    private UserEntity user;

    private LocalDate checkin;
    private LocalDate checkout;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    private int people;
    private int price;
    private String status = "예약 중";

    // 👇 [추가] Reservation 엔티티가 저장되기 전에 실행될 메서드
    @PrePersist
    public void createOrderId() {
        SecureRandom random = new SecureRandom();
        // 10000000부터 99999999까지의 난수를 생성합니다 (8자리 숫자).
        int number = 10000000 + random.nextInt(90000000);
        this.orderId = String.valueOf(number);
    }

    
}
