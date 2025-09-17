package com.example.backend.search;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "room_availability", uniqueConstraints = @UniqueConstraint(columnNames = { "r_id", "date" }))
public class RoomAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "r_id", nullable = false)
    private Room room;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "available_count", nullable = false)
    private Integer availableCount;
}
