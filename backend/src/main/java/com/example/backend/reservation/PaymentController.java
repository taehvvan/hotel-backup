package com.example.backend.reservation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.login.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/complete")
    public ResponseEntity<?> completePayment(@RequestBody PaymentRequest dto,
                                             @RequestHeader("Authorization") String authorization) {
        // JWT 토큰에서 유저 ID 추출
        String token = authorization.substring(7);  // "Bearer " 제거
        
        System.out.println("사용자 토큰: " + token);
        Integer uId = jwtTokenProvider.getUserIdFromToken(token);

        if (uId == null) {
            System.out.println("토큰에서 사용자 ID 추출 실패");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("유효하지 않은 토큰입니다.");
        }

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

