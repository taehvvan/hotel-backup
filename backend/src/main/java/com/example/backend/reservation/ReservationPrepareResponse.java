package com.example.backend.reservation;

import com.example.backend.search.HotelDTO;
import com.example.backend.search.RoomDTO;
import com.example.backend.search.Hotel;
import com.example.backend.search.Room;
import lombok.Getter;

// Lombok 어노테이션을 사용하여 간단하게 구현
@Getter
public class ReservationPrepareResponse {

    private final Integer reservationId;
    private final RoomDTO room;
    private final HotelDTO hotel;

    public ReservationPrepareResponse(Reservation reservation) {
        this.reservationId = reservation.getReId();
        this.room = new RoomDTO(reservation.getRoom());
        this.hotel = new HotelDTO(reservation.getRoom().getHotel());
    }
}