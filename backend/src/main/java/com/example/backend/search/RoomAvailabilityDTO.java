package com.example.backend.search;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomAvailabilityDTO {
    private LocalDate date;
    private int availableCount;
}
