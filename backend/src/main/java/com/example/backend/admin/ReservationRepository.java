package com.example.backend.admin;

import com.example.backend.admin.Reservation;
import com.example.backend.admin.SalesByRegion;
import com.example.backend.admin.MonthlySales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDate;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 결제 완료된 모든 예약의 총 금액을 합산
    @Query("SELECT SUM(p.price) FROM Payments p WHERE p.reId IN (SELECT r.reId FROM Reservation r WHERE r.status = '예약 완료')")
    Long sumCompletedReservationPrice();

    // 지난 한 달간의 총 매출을 합산
    @Query("SELECT SUM(p.price) FROM Payments p WHERE p.payDate >= :thirtyDaysAgo AND p.reId IN (SELECT r.reId FROM Reservation r WHERE r.status = '예약 완료')")
    Long sumMonthlyRevenue(LocalDate thirtyDaysAgo);
    
    // 지역별 매출 데이터를 가져오는 메서드
    // Payments, Reservation, Room, Hotel 테이블을 조인하여 지역별로 그룹화
    @Query("SELECT new com.example.backend.admin.dto.SalesByRegion(h.region, SUM(p.price)) " +
           "FROM Payments p " +
           "JOIN Reservation re ON p.reId = re.reId " +
           "JOIN Room r ON re.rId = r.rId " +
           "JOIN Hotel h ON r.hId = h.hId " +
           "WHERE re.status = '예약 완료' GROUP BY h.region")
    List<SalesByRegion> findSalesByRegion();

    // 월별 총 매출을 가져오는 메서드
    // Payments, Reservation 테이블을 조인하여 월별로 그룹화
    @Query("SELECT new com.example.backend.admin.dto.MonthlySales(FUNCTION('DATE_FORMAT', p.payDate, '%Y-%m'), SUM(p.price)) " +
           "FROM Payments p " +
           "JOIN Reservation r ON p.reId = r.reId " +
           "WHERE r.status = '예약 완료' GROUP BY FUNCTION('DATE_FORMAT', p.payDate, '%Y-%m')")
    List<MonthlySales> findMonthlySales();
}
