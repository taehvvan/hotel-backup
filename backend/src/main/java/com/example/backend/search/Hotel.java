package com.example.backend.search;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Hotel {

    @Id
    @Column(name = "h_id")
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
    private List<Review> reviews;

    @ManyToMany
    @JoinTable(name = "hotel_service", joinColumns = @JoinColumn(name = "h_id"), inverseJoinColumns = @JoinColumn(name = "service_id"))
    private List<ServiceEntity> services;
}