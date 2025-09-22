package com.example.backend.login;

import com.example.backend.login.dto.LoginRequest;
import com.example.backend.login.dto.TokenResponse;
import com.example.backend.login.security.PrincipalDetailsService;
import com.example.backend.login.security.jwt.JwtTokenProvider;
import com.example.backend.login.security.jwt.LoginDTO;

import java.util.NoSuchElementException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.backend.register.UserEntity;
import com.example.backend.register.UserRepository;

import jakarta.validation.Valid;

import com.example.backend.register.UserService;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PrincipalDetailsService principalDetailsService;

    @Autowired
    public LoginController(@Lazy AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            UserRepository userRepository,
            UserService userService,
            PrincipalDetailsService principalDetailsService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.userService = userService;
        this.principalDetailsService = principalDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        UserDetails userDetails = principalDetailsService.loadUserByUsername(loginRequest.getEmail());

        if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        // 강제로 Authentication 객체를 만들어 SecurityContext에 넣기
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserEntity user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail(),
                user.getRole(), user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail(), user.getId());

        userService.saveRefreshToken(user, refreshToken);

        return ResponseEntity.ok(new TokenResponse("Bearer", accessToken, refreshToken,
                jwtTokenProvider.getAccessTokenExpirationInMilliSeconds()));
    }

    @GetMapping("/me")
    public ResponseEntity<LoginDTO> getCurrentUser(@RequestHeader("Authorization") String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");
        String email = jwtTokenProvider.getUsernameFromToken(token);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
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
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        ;
        if (user == null || !refreshToken.equals(user.getRefreshToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(email, user.getRole(), user.getId());

        // TokenResponse 객체 생성
        TokenResponse tokenResponse = new TokenResponse(
                "Bearer",
                newAccessToken,
                refreshToken, // 기존 리프레시 토큰 그대로 반환
                jwtTokenProvider.getAccessTokenExpirationInMilliSeconds());

        return ResponseEntity.ok(tokenResponse);
    }
}
