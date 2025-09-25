package com.example.backend.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardSummary {
    private Long totalRevenue;
    private Long monthlyFee;
    private Integer newUsers;
    private Integer pendingHotels;
}
