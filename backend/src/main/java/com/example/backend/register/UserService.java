package com.example.backend.register;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.login.dto.TokenResponse;
import com.example.backend.login.security.jwt.JwtTokenProvider;


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

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

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

    public void saveRefreshToken(UserEntity user, String refreshToken) {
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
    }

    // 리프레시 토큰으로 액세스 토큰 재발급
    public TokenResponse refreshAccessToken(String refreshToken) {
        UserEntity user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRole());
        return new TokenResponse("Bearer", newAccessToken, refreshToken, jwtTokenProvider.getAccessTokenExpirationInMilliSeconds());
    }
    
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public UserEntity googleLoginOrRegister(String code) throws Exception {
        String idToken = googleService.getIdToken(code);
        UserEntity googleUser = googleService.getUserInfo(idToken);
    
        Optional<UserEntity> existingUser = userRepository.findByEmailAndSocial(googleUser.getEmail(), "google");
    
        return existingUser.orElseGet(() -> {
            googleUser.setPassword(null);
            googleUser.setSocial("google");
            return userRepository.save(googleUser);
        });
    }
}

