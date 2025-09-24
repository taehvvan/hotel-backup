package com.example.backend.reservation;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    @Query("SELECT new com.example.backend.reservation.ReservationResponseDTO(" +
           "r.reId, r.checkin, r.checkout, r.people, r.price, r.status, " +
           "rm.type, " +
           "h.hName, h.address, 'https://placehold.co/300x200?text=Hotel+Image') " +
           "FROM Reservation r " +
           "LEFT JOIN r.room rm " + // <-- INNER JOIN에서 LEFT JOIN으로 변경
           "LEFT JOIN rm.hotel h " + // <-- INNER JOIN에서 LEFT JOIN으로 변경
           "WHERE r.user.id = :userId " +
           "ORDER BY r.checkin DESC")
    List<ReservationResponseDTO> findMyReservationsByUserId(@Param("userId") Integer userId);

    Reservation findByReId(Integer reId);

    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId")
    List<Reservation> findRawReservationsByUserIdForDebug(@Param("userId") Integer userId);
}
