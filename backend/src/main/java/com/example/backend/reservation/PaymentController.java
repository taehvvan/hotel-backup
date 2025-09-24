package com.example.backend.reservation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.backend.login.security.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    @PostMapping("/complete")
    public ResponseEntity<?> completePayment(@RequestBody PaymentRequest dto,
                                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
        }

        // PrincipalDetails → UserEntity → u_id
        Integer uId = principalDetails.getUser().getId();

        Payment payment = paymentService.completePayment(
                uId,
                dto.getRId(),
                dto.getReId(),
                dto.getPayMethod()
        );

        if (payment != null) {
            paymentRepository.save(payment);  // 결제 정보 저장
        }

        return ResponseEntity.ok(payment);
    }
}
