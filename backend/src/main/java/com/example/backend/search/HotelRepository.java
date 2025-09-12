package com.example.backend.search;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

  @Query(value = """
          SELECT DISTINCT h.*
          FROM hotel h
          JOIN room r ON h.h_id = r.h_id
          WHERE h.region = :region
            AND r.count >= :numberOfRooms
            AND (r.people * :numberOfRooms) >= :numberOfPeople
            AND NOT EXISTS (
                SELECT 1
                FROM room_availability ra
                WHERE ra.r_id = r.r_id
                AND ra.date BETWEEN :startDate AND :endDate
                AND ra.available_count < :numberOfRooms
            )
      """, nativeQuery = true)
  List<Hotel> searchHotels(
      @Param("region") String region,
      @Param("numberOfPeople") int numberOfPeople,
      @Param("numberOfRooms") int numberOfRooms,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);
}