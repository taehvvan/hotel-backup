package com.example.backend.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.backend.register.UserDTO;
import com.example.backend.register.UserEntity;
import com.example.backend.register.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean registerUser(UserDTO userDto) {
        try {
            // DTO를 Entity로 변환
            UserEntity user = new UserEntity();
            user.setEmail(userDto.getEmail());
            user.setName(userDto.getName()); // UserDTO에 username 필드가 있다면 사용
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));

            // 사용자 저장
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            System.err.println("회원가입 처리 중 오류: " + e.getMessage());
            return false;
        }
    }
}
