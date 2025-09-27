package com.example.backend.manager;

import com.example.backend.login.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/manager") // 관리자 전용 API 경로
@RequiredArgsConstructor
public class ManagerReservationController {

    private final ManagerReservationService managerReservationService;

    @GetMapping("/reservations")
    public ResponseEntity<List<ManagerReservationDTO>> getManagerReservations(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        
        if (principalDetails == null || principalDetails.getUser() == null) {
            return ResponseEntity.status(401).build(); // 인증되지 않은 사용자
        }

        // 1. 로그인 정보에서 사업자 등록번호(business_number)를 가져옵니다.
        String businessNumber = principalDetails.getUser().getBusinessNumber();

        // 2. 서비스를 호출하여 예약 목록을 가져옵니다.
        List<ManagerReservationDTO> reservations = managerReservationService.getAllReservationsForManager(businessNumber);

        // 3. 조회된 예약 목록을 반환합니다.
        return ResponseEntity.ok(reservations);
    }
}