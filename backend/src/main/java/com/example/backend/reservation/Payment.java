package com.example.backend.reservation;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "p_id")
    private Integer pId;

    @JoinColumn(name = "u_id")
    private Integer uId;

    @JoinColumn(name = "r_id")
    private Integer rId;

    @JoinColumn(name = "re_id")
    private Integer reId;

    @Column(name = "pay_method")
    private String payMethod;

    @CreationTimestamp
    @Column(name = "pay_date")
    private LocalDateTime payDate = LocalDateTime.now();
}

