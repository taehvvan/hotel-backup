package com.example.backend.detail;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.example.backend.search.Hotel;
import com.example.backend.search.HotelDTO;
import com.example.backend.search.HotelRepository;
import com.example.backend.search.Room;
import com.example.backend.search.RoomAvailability;
import com.example.backend.search.RoomDTO;
import com.example.backend.search.ServiceDTO;
import com.example.backend.search.Review;
import com.example.backend.search.ReviewDTO;

@Service
public class HotelDetailService {

    private final HotelRepository hotelRepository;

    public HotelDetailService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public HotelDTO detail(DetailRequest request) {
        // Safe null check for hId
        if (request.getHId() == null) {
            throw new IllegalArgumentException("호텔 ID가 null입니다.");
        }

        // 1. Retrieve hotel basic information
        Hotel hotel = hotelRepository.findById(request.getHId())
                .orElseThrow(() -> new RuntimeException("호텔을 찾을 수 없습니다."));

        // 2. Filter rooms based on the provided date, number of people, and rooms
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("시작일 또는 종료일이 null입니다.");
        }

        List<Room> availableRooms = hotel.getRooms().stream()
                .filter(r -> isRoomAvailable(r, startDate, endDate, request.getNumberOfRooms(),
                        request.getNumberOfPeople()))
                .collect(Collectors.toList());

        // 3. Convert to DTO
        return convertToDTO(hotel, availableRooms);
    }

    private boolean isRoomAvailable(Room room, LocalDate start, LocalDate end, int numberOfRooms, int numberOfPeople) {
        // 1. Check if the room count and total capacity meet the request
        if (room.getCount() < numberOfRooms || room.getPeople() * room.getCount() < numberOfPeople) {
            return false;
        }

        // 2. Fetch all availability data within the requested date range
        List<RoomAvailability> availabilities = room.getRoomAvailabilities().stream()
                .filter(a -> !a.getDate().isBefore(start) && !a.getDate().isAfter(end.minusDays(1)))
                .collect(Collectors.toList());

        // 3. Check for insufficient availability, matching the SQL's NOT EXISTS logic.
        // The room is considered unavailable if there is at least one date with
        // insufficient rooms.
        boolean hasInsufficientAvailability = availabilities.stream()
                .anyMatch(a -> a.getAvailableCount() < numberOfRooms);

        // Return true if no insufficient availability was found, otherwise return
        // false.
        // This also handles cases with no availability data (anyMatch returns false).
        return !hasInsufficientAvailability;
    }

    private HotelDTO convertToDTO(Hotel hotel, List<Room> availableRooms) {
        HotelDTO dto = new HotelDTO();
        dto.setHId(hotel.getHId());
        dto.setHName(hotel.getHName());
        dto.setRegion(hotel.getRegion());
        dto.setType(hotel.getType());
        dto.setAddress(hotel.getAddress());
        dto.setStar(hotel.getStar());
        dto.setActive(hotel.getActive());
        dto.setInfo(hotel.getInfo());

        // Calculate minPrice from available rooms
        Integer minPrice = availableRooms.stream()
                .map(Room::getPrice)
                .min(Integer::compareTo)
                .orElse(0);
        dto.setMinPrice(minPrice);

        // Handle reviews (null-safe)
        List<Review> reviews = Optional.ofNullable(hotel.getReviews()).orElseGet(ArrayList::new);
        if (!reviews.isEmpty()) {
            int sum = reviews.stream().mapToInt(Review::getScore).sum();
            int count = reviews.size();
            dto.setAvgScore(sum / (double) count);
            dto.setReviewCount(count);
        } else {
            dto.setAvgScore(0.0);
            dto.setReviewCount(0);
        }
        dto.setReviews(hotel.getReviews().stream()
                .map(review -> {
                    ReviewDTO rDto = new ReviewDTO();
                    rDto.setReviewId(review.getReviewId());
                    rDto.setPId(review.getPId());
                    rDto.setHId(review.getHotel().getHId()); // Hotel 참조는 ID만
                    rDto.setImage(review.getImage());
                    rDto.setScore(review.getScore());
                    rDto.setContent(review.getContent());
                    return rDto;
                }).collect(Collectors.toList()));

        // Convert Rooms to Room DTOs
        dto.setRooms(availableRooms.stream().map(room -> {
            RoomDTO roomDTO = new RoomDTO();
            roomDTO.setRId(room.getRId());
            roomDTO.setType(room.getType());
            roomDTO.setPrice(room.getPrice());
            roomDTO.setPeople(room.getPeople());
            roomDTO.setCount(room.getCount());
            roomDTO.setInfo(room.getInfo());
            roomDTO.setCheckinTime(room.getCheckinTime());
            roomDTO.setCheckoutTime(room.getCheckoutTime());
            return roomDTO;
        }).collect(Collectors.toList()));

        // Convert Hotel Services to Service DTOs
        dto.setServices(hotel.getServices().stream()
                .map(s -> {
                    ServiceDTO serviceDTO = new ServiceDTO();
                    serviceDTO.setServiceName(s.getServiceName());
                    return serviceDTO;
                })
                .collect(Collectors.toList()));

        return dto;
    }
}