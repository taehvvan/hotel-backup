package com.example.backend.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/dashboard/summary")
    public ResponseEntity<DashboardSummary> getDashboardSummary(){
        DashboardSummary summary = adminDashboardService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("dashboard/sales-by-region")
    public ResponseEntity<List<SalesByRegion>> getSalesByRegion() {
        List<SalesByRegion> salesData = adminDashboardService.getSalesByRegion();
        return ResponseEntity.ok(salesData);
    }
    
    @GetMapping("/dashboard/monthly-sales")
    public ResponseEntity<List<MonthlySales>> getMonthlySales() {
        List<MonthlySales> salesData = adminDashboardService.getMonthlySales();
        return ResponseEntity.ok(salesData);
    }

}
