package com.example.backend.login;

import com.example.backend.login.dto.LoginRequest;
import com.example.backend.login.dto.TokenResponse;
import com.example.backend.login.security.jwt.JwtTokenProvider;
import com.example.backend.login.security.jwt.LoginDTO;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.backend.register.UserEntity;
import com.example.backend.register.UserRepository;
import com.example.backend.register.UserService;

import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public LoginController(@Lazy AuthenticationManager authenticationManager,
     JwtTokenProvider jwtTokenProvider,
     UserRepository userRepository,
     UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserEntity user = userRepository.findByEmail(loginRequest.getEmail());

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail(),
                user.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        userService.saveRefreshToken(user, refreshToken);

        return ResponseEntity.ok(new TokenResponse("Bearer", accessToken, refreshToken,
                jwtTokenProvider.getAccessTokenExpirationInMilliSeconds()));
    }

    @GetMapping("/me")
    public ResponseEntity<LoginDTO> getCurrentUser(@RequestHeader("Authorization") String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");
        String email = jwtTokenProvider.getUsernameFromToken(token);
        UserEntity user = userRepository.findByEmail(email);
        LoginDTO dto = new LoginDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        // 리프레시 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 토큰에서 사용자 정보 추출
        String email = jwtTokenProvider.getUsernameFromToken(refreshToken);
        UserEntity user = userRepository.findByEmail(email);
        if (user == null || !refreshToken.equals(user.getRefreshToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(email, user.getRole());

        // TokenResponse 객체 생성
        TokenResponse tokenResponse = new TokenResponse(
                "Bearer",
                newAccessToken,
                refreshToken, // 기존 리프레시 토큰 그대로 반환
                jwtTokenProvider.getAccessTokenExpirationInMilliSeconds()
        );

        return ResponseEntity.ok(tokenResponse);
    }
}

