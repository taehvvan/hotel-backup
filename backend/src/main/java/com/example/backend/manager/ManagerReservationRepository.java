package com.example.backend.manager;

// ▼▼▼ [수정 1] 필요한 클래스들의 '주소'를 알려주는 import 구문 추가 ▼▼▼
import com.example.backend.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// ▼▼▼ [수정 2] Reservation의 ID 타입이 Integer이므로 Long을 Integer로 변경 ▼▼▼
public interface ManagerReservationRepository extends JpaRepository<Reservation, Integer> { 

    @Query("SELECT new com.example.backend.manager.ManagerReservationDto(" +
           "r.reId, u.name, h.hName, ro.type, r.checkin, r.checkout, r.status) " +
           "FROM Reservation r " +
           "JOIN r.user u " +
           "JOIN r.room ro " +
           "JOIN ro.hotel h " +
           "WHERE (:status = 'all' OR r.status = :status) " +
           "AND (:guestName IS NULL OR u.name LIKE %:guestName%) " +
           "AND (:reId IS NULL OR r.reId = :reId) " +
           "ORDER BY r.checkin DESC")
    List<ManagerReservationDto> findReservationsWithDetails(
            @Param("status") String status,
            @Param("guestName") String guestName,
            // ▼▼▼ [수정 3] 파라미터 타입도 Long을 Integer로 변경 ▼▼▼
            @Param("reId") Integer reId 
    );
}