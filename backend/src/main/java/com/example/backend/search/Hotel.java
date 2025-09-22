package com.example.backend.search;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import com.example.backend.manager.HotelPic;
import com.example.backend.wishlist.DibsEntity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Hotel {

    @Id
    @Column(name = "h_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 이 부분을 추가해야 합니다.
    private Long hId;

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

    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "info")
    private String info;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "hotel_service", joinColumns = @JoinColumn(name = "h_id"), inverseJoinColumns = @JoinColumn(name = "service_id"))
    private List<ServiceEntity> services;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DibsEntity> dibsList = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HotelPic> images = new ArrayList<>();
}