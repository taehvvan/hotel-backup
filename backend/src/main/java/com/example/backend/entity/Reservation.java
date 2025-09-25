package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
@Data
@NoArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "re_id")
    private Integer reId;

    @Column(name = "r_id")
    private Integer rId;

    @Column(name = "u_id")
    private Integer uId;

    @Column(name = "checkin")
    private LocalDateTime checkin;

    @Column(name = "checkout")
    private LocalDateTime checkout;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "people")
    private Integer people;

    @Column(name = "price")
    private Integer price;

    @Column(name = "status")
    private String status;
}
