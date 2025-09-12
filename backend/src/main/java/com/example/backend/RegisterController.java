package com.example.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RegisterController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseMessage> registerUser(@RequestBody User user) {
        try {
            boolean isRegistered = userService.registerUser(user);

            if (isRegistered) {
                // 회원가입 성공
                ResponseMessage res = new ResponseMessage(true, "회원가입 성공!");
                return ResponseEntity.ok(res);
            } else {
                // 이미 존재하는 이메일
                ResponseMessage res = new ResponseMessage(false, "이미 존재하는 이메일입니다.");
                return ResponseEntity.badRequest().body(res);
            }
        } catch (Exception e) {
            // 예외 처리
            ResponseMessage res = new ResponseMessage(false, "회원가입 오류: " + e.getMessage());
            return ResponseEntity.status(500).body(res);
        }
    }
    
}

class ResponseMessage {
    private boolean success;
    private String message;

    // 기본 생성자
    public ResponseMessage() {
    }

    // 생성자
    public ResponseMessage(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // getter and setter for success
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    // getter and setter for message
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // toString() 메서드 (선택사항)
    @Override
    public String toString() {
        return "ResponseMessage{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
