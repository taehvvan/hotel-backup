package com.example.backend.search;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelDTO {
    private Long hId;
    private String hName;
    private String region;
    private List<RoomDTO> rooms;
}
