package com.example.backend.search;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Room {

    @Id
    @Column(name = "r_id")
    private Long rId;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "count", nullable = false)
    private int count;

    @Column(name = "people", nullable = false)
    private int people;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "h_id", nullable = false)
    private Hotel hotel;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomAvailability> roomAvailabilities;
}