package com.example.backend.mypage;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.mailcheck.VerificationService;
import com.example.backend.register.UserEntity;
import com.example.backend.register.UserRepository;

@RestController
@RequestMapping("/api/users")
public class MyPageUserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/info") //로그인한 유저 정보 가져옴
    public ResponseEntity<UserEntity> getUserInfo(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("유저를 찾지 못했습니다."));

        return ResponseEntity.ok(user);
    }

    @PutMapping("phone")//전화번호 입력
    public ResponseEntity<Map<String, String>> updatePhoneNumber(@RequestBody Map<String,String> request ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        String newPhone = request.get("phone");

        UserEntity user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("유저를 찾지 못했습니다."));

        user.setPhone(newPhone);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "전화번호가 성공적으로 변경되었습니다."));
    }

    //비밀번호 변경
    @PutMapping("/password")
    public ResponseEntity<Map<String, String>> changePasswordWithOldPassword(@RequestBody Map<String,String> request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String eamil = authentication.getName();

        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        UserEntity user = userRepository.findByEmail(eamil).orElseThrow(() -> new RuntimeException("유저를 찾지 못했습니다."));

        if(!passwordEncoder.matches(oldPassword,user.getPassword())){
            return ResponseEntity.badRequest().body(Map.of("message", "기존 비밀번호가 일치하지 않습니다."));
        }

        String encryptedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encryptedNewPassword);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "비밀번호가 성공적으로 변경되었습니다."));
    }

    @PostMapping("/verify-password")
    public ResponseEntity<Map<String, String>> verifyOldPassword(
    @RequestBody PasswordChangeRequestDTO request) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();
    String oldPassword = request.getOldPassword();

    // 1. 현재 로그인된 사용자의 정보를 가져옵니다.
    UserEntity user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("유저를 찾지 못했습니다."));

    // 2. 요청받은 기존 비밀번호와 저장된 비밀번호를 비교합니다.
    if (passwordEncoder.matches(oldPassword, user.getPassword())) {
        return ResponseEntity.ok(Map.of("message", "비밀번호가 확인되었습니다."));
    } else {
        return ResponseEntity.badRequest().body(Map.of("message", "기존 비밀번호가 일치하지 않습니다."));
    }
}
}