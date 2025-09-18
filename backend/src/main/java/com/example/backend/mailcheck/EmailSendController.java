package com.example.backend.mailcheck;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EmailSendController {

    //private final EmailSendService emailSendService;
    private final VerificationService verificationService;

    /* 
    public EmailSendContoller(EmailSendService emailSendService, VerificationService verificationService){
        this.emailSendService = emailSendService;
        this.verificationService = verificationService;
    }
    */

    public EmailSendController(VerificationService verificationService){
        this.verificationService = verificationService;
    }

    @PostMapping("/send-code")
    public ResponseEntity<Map<String, String>> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "이메일 주소를 입력해주세요."));
        }

        try {
            // VerificationService를 통해 인증번호 생성 및 이메일 전송
            verificationService.sendVerificationCode(email);
            return ResponseEntity.ok(Map.of("message", "인증번호가 이메일로 전송되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "이메일 전송에 실패했습니다."));
        }
    }

    // ---

    // 이메일 인증번호 확인 API
    // 요청 본문: { "email": "user@example.com", "code": "123456" }
    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, String>> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        if (email == null || email.trim().isEmpty() || code == null || code.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "이메일 또는 인증번호를 입력해주세요."));
        }

        // VerificationService를 통해 인증번호 일치 여부 확인
        boolean isVerified = verificationService.verifyCode(email, code);

        if (isVerified) {
            return ResponseEntity.ok(Map.of("message", "이메일 인증이 완료되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "인증번호가 일치하지 않거나 만료되었습니다."));
        }
    }
}
