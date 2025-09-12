package com.example.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    public boolean registerUser(User user) {
        try {
            // 사용자 저장
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("회원가입 처리 중 오류: " + e.getMessage());
        }
    }
}
