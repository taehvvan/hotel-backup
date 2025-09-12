package com.example.backend.search;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "hotel", schema = "H")
public class Hotel {

    @Id
    @Column(name = "h_id")
    private Long hId;

    @Column(name = "h_name", nullable = false)
    private String hName;

    @Column(name = "region", nullable = false)
    private String region;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms;
}