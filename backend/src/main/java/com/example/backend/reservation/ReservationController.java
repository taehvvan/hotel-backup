package com.example.backend.reservation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.backend.login.security.PrincipalDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    // ✅ ResponseEntity의 반환 타입을 ReservationPrepareResponse 로 변경
    public ResponseEntity<ReservationPrepareResponse> prepareReservation(@RequestBody ReservationRequest request) {

        System.out.println("---------- [BACKEND-CONTROLLER] 예약 준비 요청 수신 ----------");
        System.out.println("수신된 ReservationRequest DTO: " + request);
 
        // ✅ 수정된 서비스 메소드를 호출합니다.
        ReservationPrepareResponse response = reservationService.createReservation(request);
        
        // ✅ 프론트엔드가 사용하기 편한 형식의 DTO를 반환합니다.
        return ResponseEntity.ok(response);
    }

    // ... (다른 GET 메소드들은 기존과 동일) ...
    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> getUserReservations(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        
        if (principalDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        Integer userId = principalDetails.getUser().getId();
        List<ReservationResponseDTO> reservations = reservationService.findMyReservations(userId);
        
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/guest")
    public ResponseEntity<ReservationResponseDTO> getGuestReservation(
            @RequestParam Integer pId, @RequestParam String phone) {
        ReservationResponseDTO reservation = reservationService.findGuestReservation(pId, phone);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/guest/detail")
    public ResponseEntity<ReservationResponseDTO> getGuestReservationDetail(
            @RequestParam Integer reservationId) {
        Reservation reservation = reservationService.findReservationById(reservationId);
        if (reservation == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ReservationResponseDTO(reservation));
    }
}
