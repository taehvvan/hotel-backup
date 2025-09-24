package com.example.backend.search;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomAvailabilityDTO {
    private LocalDate date;
    private int availableCount;
}
