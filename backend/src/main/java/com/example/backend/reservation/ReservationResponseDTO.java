package com.example.backend.reservation;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 마이페이지 등에서 사용자의 예약 내역을 보여주기 위한 DTO.
 * Reservation 엔티티를 기반으로 생성됩니다.
 */
@Getter
@Builder
public class ReservationResponseDTO {

    // Reservation 정보
    private final Integer reservationId;
    private String orderId;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final int people;
    private final int price;
    private final String status;

    // Room 정보
    private final String roomType;

    // Hotel 정보
    private final String hotelName;
    private final String address;
    private final String hotelImage; // 호텔 대표 이미지

    // JPQL에서 사용할 생성자
    public ReservationResponseDTO(Integer reservationId, String orderId, LocalDate checkIn, LocalDate checkOut, int people, int price, String status, String roomType, String hotelName, String address, String hotelImage) {
        this.reservationId = reservationId;
        this.orderId = orderId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.people = people;
        this.price = price;
        this.status = status;
        this.roomType = roomType;
        this.hotelName = hotelName;
        this.address = address;
        this.hotelImage = hotelImage;
    }

    public ReservationResponseDTO(Reservation reservation) {
        this.reservationId = reservation.getReId();
        this.orderId = reservation.getOrderId();
        this.checkIn = reservation.getCheckin();
        this.checkOut = reservation.getCheckout();
        this.people = reservation.getPeople();
        this.price = reservation.getPrice();
        this.status = reservation.getStatus();
    
        this.roomType = reservation.getRoom() != null ? reservation.getRoom().getType() : "";
        this.hotelName = (reservation.getRoom() != null && reservation.getRoom().getHotel() != null) 
                         ? reservation.getRoom().getHotel().getHName() : "";
        this.address = (reservation.getRoom() != null && reservation.getRoom().getHotel() != null)
                         ? reservation.getRoom().getHotel().getAddress() : "";

        // 👇 [수정] 이미지 경로 생성 로직 변경
        // 로컬 경로(D:/hotel_images)를 웹 URL(http://localhost:8888/images)로 교체합니다.
        if (reservation.getRoom() != null && reservation.getRoom().getHotel() != null
                && reservation.getRoom().getHotel().getImages() != null
                && !reservation.getRoom().getHotel().getImages().isEmpty()) {
            
            String originalUrl = reservation.getRoom().getHotel().getImages().get(0).getImageUrl();
            
            // "D:/hotel_images" 부분을 "http://localhost:8888/images"로 교체합니다.
            // Windows 경로(\)와 Unix 경로(/)를 모두 처리하도록 수정합니다.
            this.hotelImage = originalUrl != null 
                ? originalUrl.replaceAll("\\\\", "/").replace("D:/hotel_images", "http://localhost:8888/images") 
                : "";

        } else {
            this.hotelImage = ""; // 이미지가 없는 경우 빈 문자열
        }
    }
}