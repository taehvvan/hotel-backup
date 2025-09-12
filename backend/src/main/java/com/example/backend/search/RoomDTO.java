package com.example.backend.search;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomDTO {
    private Long rId;
    private String type;
    private int count;
    private int people;
    // RoomAvailability가 필요하면 포함 가능
    private List<RoomAvailabilityDTO> availabilities;
}
