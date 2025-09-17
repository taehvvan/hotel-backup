package com.example.backend.search;

import java.time.LocalDate;
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
    private int price; // 추가
    private String info; // 선택 사항
    private LocalDate checkinTime; // 선택 사항
    private LocalDate checkoutTime; // 선택 사항
    private Long hotelId; // 선택 사항
    private List<RoomAvailabilityDTO> availabilities;
}
