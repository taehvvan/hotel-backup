package com.example.backend.manager;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ManagerReservationDTO {
    private Integer reservationId;
    private String userName;
    private String hotelName;
    private String roomType;
    private String checkIn;
    private String checkOut;
    private String status;
}