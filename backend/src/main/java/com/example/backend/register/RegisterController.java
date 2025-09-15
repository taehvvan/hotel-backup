package com.example.backend.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RegisterController {

    
    private final UserService userService;

    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Boolean>> registerUser(@RequestBody UserDTO userDto) {
        boolean isRegistered = userService.registerUser(userDto);
        if (isRegistered) {
            // 성공 시 success: true를 포함하는 JSON 응답 반환
            return ResponseEntity.ok(Collections.singletonMap("success", true));
        } else {
            // 실패 시 success: false를 포함하는 JSON 응답 반환 (400 Bad Request)
            return ResponseEntity.badRequest().body(Collections.singletonMap("success", false));
        }
    }
    
}

