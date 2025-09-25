package com.example.backend.admin;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.register.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final ReservationRepository reservationRepository;
    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;

    public DashboardSummary getDashboardSummary() {
        // 1. 총 플랫폼 매출 (결제 완료된 예약 금액 합산)
        Long totalRevenue = reservationRepository.sumCompletedReservationPrice();

        // 2. 월간 수수료 수익 (총 매출의 10%로 가정)
        Long monthlyFee = (long) (totalRevenue * 0.1);
        
        // 3. 신규 가입자 수 (예: 지난 30일 기준)
        Integer newUsers = userRepository.countNewUsersLast30Days();

        // 4. 신규 호텔 승인 대기 건수
        Integer pendingHotels = managerRepository.countPendingHotels();

        return new DashboardSummary(totalRevenue, monthlyFee, newUsers, pendingHotels);
    }

    public List<SalesByRegion> getSalesByRegion() {
        // 지역별 매출 데이터를 가져오는 로직 (예: DB에서 그룹화하여 조회)
        return reservationRepository.findSalesByRegion();
    }

    public List<MonthlySales> getMonthlySales() {
        // 월별 매출 데이터를 가져오는 로직
        return reservationRepository.findMonthlySales();
    }

}
