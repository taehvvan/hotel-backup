package com.example.backend.reservation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.backend.login.security.PrincipalDetails;
import com.example.backend.register.UserEntity;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/complete")
    public ResponseEntity<?> completePayment(@RequestBody PaymentRequest dto,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails) {

        UserEntity user = principalDetails != null ? principalDetails.getUser() : null;

        System.out.println("디버깅 - reId: " + dto.getReId()); // 로그 확인

        Payment payment = paymentService.completePayment(
                user,
                dto.getReId(),
                dto.getPayMethod(),
                dto.getPhone()
        );

        return ResponseEntity.ok(payment);
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
