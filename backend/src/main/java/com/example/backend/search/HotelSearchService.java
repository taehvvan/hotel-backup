package com.example.backend.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 호텔 검색을 위한 비즈니스 로직을 처리하는 서비스 클래스.
 * HotelRepository를 사용하여 조건에 맞는 호텔을 검색합니다.
 */
@Service
public class HotelSearchService {

    private final HotelRepository hotelRepository;

    @Autowired
    public HotelSearchService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    /**
     * 클라이언트로부터 받은 검색 조건에 따라 호텔을 검색하고 DTO로 변환합니다.
     *
     * @param request 검색 요청 객체
     * @return 검색 조건에 맞는 호텔 목록 (DTO)
     */
    public List<HotelDTO> search(SearchRequest request) {
        List<Hotel> hotels = hotelRepository.searchHotels(
                request.getRegion(),
                request.getNumberOfPeople(),
                request.getNumberOfRooms(),
                request.getStartDate(),
                request.getEndDate());

        // 엔티티 → DTO 변환
        return hotels.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Hotel 엔티티를 HotelDTO로 변환
     */
    private HotelDTO convertToDTO(Hotel hotel) {
        HotelDTO dto = new HotelDTO();
        dto.setHId(hotel.getHId());
        dto.setHName(hotel.getHName());
        dto.setType(hotel.getType());
        dto.setRegion(hotel.getRegion());
        dto.setAddress(hotel.getAddress());
        dto.setStar(hotel.getStar());
        dto.setInfo(hotel.getInfo());

        // services 변환
        List<ServiceDTO> serviceDTOs = hotel.getServices().stream().map(service -> {
            ServiceDTO sDto = new ServiceDTO();
            sDto.setServiceName(service.getServiceName());
            return sDto;
        }).collect(Collectors.toList());
        dto.setServices(serviceDTOs);

        // rooms 처리
        if (hotel.getRooms() != null && !hotel.getRooms().isEmpty()) {
            List<RoomDTO> roomDTOs = hotel.getRooms().stream().map(room -> {
                RoomDTO rDto = new RoomDTO();
                rDto.setRId(room.getRId());
                rDto.setType(room.getType());
                rDto.setCount(room.getCount());
                rDto.setPeople(room.getPeople());
                rDto.setPrice(room.getPrice());
                rDto.setInfo(room.getInfo());
                rDto.setCheckinTime(room.getCheckinTime());
                rDto.setCheckoutTime(room.getCheckoutTime());

                if (room.getRoomAvailabilities() != null) {
                    rDto.setAvailabilities(
                            room.getRoomAvailabilities().stream().map(ra -> {
                                RoomAvailabilityDTO raDto = new RoomAvailabilityDTO();
                                raDto.setDate(ra.getDate());
                                raDto.setAvailableCount(ra.getAvailableCount());
                                return raDto;
                            }).collect(Collectors.toList()));
                } else {
                    rDto.setAvailabilities(new ArrayList<>());
                }

                return rDto;
            }).collect(Collectors.toList());
            dto.setRooms(roomDTOs);

            // 최저가 계산
            Integer minPrice = roomDTOs.stream()
                    .map(RoomDTO::getPrice)
                    .min(Integer::compareTo)
                    .orElse(0); // 방이 없으면 0
            dto.setMinPrice(minPrice);
        } else {
            dto.setRooms(new ArrayList<>());
            dto.setMinPrice(0);
        }

        // 리뷰 처리
        if (hotel.getReviews() != null && !hotel.getReviews().isEmpty()) {
            int sum = hotel.getReviews().stream().mapToInt(Review::getScore).sum();
            int count = hotel.getReviews().size();
            dto.setAvgScore(sum / (double) count);
            dto.setReviewCount(count);
        } else {
            dto.setAvgScore(0.0);
            dto.setReviewCount(0);
        }

        return dto;
    }
}