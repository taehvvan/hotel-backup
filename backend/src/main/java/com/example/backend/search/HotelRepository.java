package com.example.backend.search;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.admin.AdminHotelDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;



@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    @Query(value = """
            SELECT DISTINCT h.*
            FROM hotel h
            JOIN room r ON h.h_id = r.h_id
            /* ✅ [반영] '=' 대신 'LIKE'를 사용하여 포함 검색 */
            WHERE h.region LIKE CONCAT('%', :region, '%')
              AND h.active = true
              AND r.count >= :numberOfRooms
              AND (r.people * :numberOfRooms) >= :numberOfPeople
              
              /* ✅ [수정] 날짜별 재고를 확인하는 로직 주석 해제 */
              AND NOT EXISTS (
                  SELECT 1
                  FROM room_availability ra
                  WHERE ra.r_id = r.r_id
                    AND ra.date BETWEEN :startDate AND :endDate
                    AND ra.available_count < :numberOfRooms
              )
            """, nativeQuery = true)
    // ✅ [수정] 날짜 파라미터를 다시 받도록 시그니처 수정
    List<Hotel> searchHotels(
            @Param("region") String region,
            @Param("numberOfPeople") int numberOfPeople,
            @Param("numberOfRooms") int numberOfRooms,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
            SELECT MIN(r.price)
            FROM Room r
            WHERE r.hotel.hId = :hotelId
              AND r.count >= :numberOfRooms
              AND (r.people * :numberOfRooms) >= :numberOfPeople
              AND NOT EXISTS (
                SELECT 1
                FROM RoomAvailability ra
                WHERE ra.room = r
                  AND ra.date BETWEEN :startDate AND :endDate
                  AND ra.availableCount < :numberOfRooms
              )
            """)
    Integer findMinPriceByHotelAndConditions(
            @Param("hotelId") Long hotelId,
            @Param("numberOfRooms") int numberOfRooms,
            @Param("numberOfPeople") int numberOfPeople,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

            

 @Query("SELECT new com.example.backend.admin.AdminHotelDto(" +
           "h.hId, h.hName, h.region, h.active, " +
           "COALESCE((SELECT SUM(res.price) FROM Reservation res WHERE res.room.hotel = h AND res.createdAt >= :monthAgo), 0L), " +
           "COALESCE((SELECT COUNT(res) FROM Reservation res WHERE res.room.hotel = h AND res.createdAt >= :monthAgo), 0L), " +
           "COALESCE((SELECT AVG(rev.score) FROM Review rev WHERE rev.hotel = h), 0.0)) " +
           "FROM Hotel h WHERE h.type = :type")
    List<AdminHotelDto> findHotelsForAdminByType(@Param("type") String type, @Param("monthAgo") LocalDateTime monthAgo);

            
    Optional<Hotel> findByhNameAndAddress(String hName, String address);
}