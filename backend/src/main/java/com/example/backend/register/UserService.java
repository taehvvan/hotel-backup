package com.example.backend.register;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GoogleService googleService;

    

    @Autowired
    private KakaoService kakaoService;

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

    public UserEntity kakaoLoginOrRegister(String code) throws Exception {
        String accessToken = kakaoService.getAccessToken(code);

        UserEntity kakaoUser = kakaoService.getUserInfo(accessToken);

        Optional<UserEntity> existingUser = userRepository.findByEmailAndSocial(kakaoUser.getEmail(), "kakao");

        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            kakaoUser.setPassword(null);
            kakaoUser.setSocial("kakao");
            return userRepository.save(kakaoUser);
        }
    }
    /* 
    public UserEntity googleLoginOrRegister(String code) throws Exception {
        String idToken = googleService.getAccessToken(code);
        User googleUser = googleService.getUserInfo(idToken);

        Optional<User> existingUser = userRepository.findByEmailAndSocial(googleUser.getEmail(), "google");
        
        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            googleUser.setPassword(null);
            googleUser.setPasswordConfirm(null);
            googleUser.setSocial("google");
            return userRepository.save(googleUser);
        }
    }
*/
}

