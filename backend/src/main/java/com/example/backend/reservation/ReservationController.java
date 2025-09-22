package com.example.backend.reservation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.example.backend.login.security.PrincipalDetails; 
import com.example.backend.reservation.ReservationResponseDTO;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationRequest request) {

        System.out.println("---------- [BACKEND-CONTROLLER] 요청 수신 ----------");
        System.out.println("수신된 ReservationRequest DTO: " + request);
 
        Reservation reservation = reservationService.createReservation(request);
        return ResponseEntity.ok(reservation);
    }

    // 현재 로그인한 사용자의 모든 예약 내역을 조회하는 엔드포인트
    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> getUserReservations(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        
        if (principalDetails == null) {
            // 인증 정보가 없는 경우 401 Unauthorized 응답
            return ResponseEntity.status(401).build();
        }
        
        Integer userId = principalDetails.getUser().getId(); // PrincipalDetails에서 사용자 ID 가져오기
        List<ReservationResponseDTO> reservations = reservationService.findMyReservations(userId);
        
        return ResponseEntity.ok(reservations);
    }
}
