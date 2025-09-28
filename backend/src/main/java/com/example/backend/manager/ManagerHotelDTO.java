package com.example.backend.manager;

import java.util.List;

import com.example.backend.search.Room;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ManagerHotelDTO {
    private Long id;
    private String name;
    private String location;
    private String type;
    private Integer stars;
    private Double latitude;
    private Double longitude;
    private List<String> amenities;
    private List<String> images;
    private String image;
    
    private List<Room> rooms; 
}