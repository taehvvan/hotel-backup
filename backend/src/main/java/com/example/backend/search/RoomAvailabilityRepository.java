package com.example.backend.search;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {

    /**
     * 특정 객실(rId)과 날짜(date)에 대한 RoomAvailability 조회
     */
    Optional<RoomAvailability> findByRoom_rIdAndDate(Integer rId, LocalDate date);

}
