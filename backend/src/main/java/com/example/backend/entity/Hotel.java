package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hotel")
@Data
@NoArgsConstructor
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "h_id")
    private Integer hId;

    @Column(name = "h_name", nullable = false)
    private String hName;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "region", nullable = false)
    private String region;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "star")
    private Integer star;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "info")
    private String info;
}
