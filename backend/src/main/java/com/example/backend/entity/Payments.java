package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
public class Payments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "p_id")
    private Integer pId;

    @Column(name = "u_id")
    private Integer uId;

    @Column(name = "r_id")
    private Integer rId;
    
    @Column(name = "re_id")
    private Integer reId;

    @Column(name = "pay_date")
    private LocalDateTime payDate;

    @Column(name = "pay_method")
    private String payMethod;
}
