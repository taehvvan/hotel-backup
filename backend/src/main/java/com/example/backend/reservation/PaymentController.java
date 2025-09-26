package com.example.backend.reservation;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.backend.login.security.PrincipalDetails;
import com.example.backend.register.UserEntity;
import com.example.backend.search.RoomAvailabilityService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final RoomAvailabilityService roomAvailabilityService;

    @PostMapping("/complete")
    public ResponseEntity<?> completePayment(@RequestBody PaymentRequest request,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        
        System.out.println("---------- [PAYMENT-CONTROLLER] 결제 완료 요청 수신 ----------");
        System.out.println("수신된 DTO: " + request);

        /*
        // Spring Security 컨텍스트에서 인증된 사용자 정보를 가져옴 (가장 신뢰할 수 있는 방법)
        UserEntity user = (principalDetails != null) ? principalDetails.getUser() : null;
        */

        // 1. 결제 완료 처리
        PaymentResponseDTO responseDTO = paymentService.completePayment(request, principalDetails);

        // 성공 응답 반환
        return ResponseEntity.ok(responseDTO);
    }

    // 비회원 예약 확인
    @GetMapping("/guest")
    public ResponseEntity<?> checkGuestReservation(@RequestParam Integer pId,
                                                   @RequestParam String phone) {
        ReservationResponseDTO dto = paymentService.findReservationForGuest(pId, phone);
        if (dto != null) return ResponseEntity.ok(dto);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("예약 정보를 찾을 수 없습니다.");
    }
}
