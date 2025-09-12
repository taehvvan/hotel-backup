package com.example.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.login.dto.LoginRequest;
import com.example.login.dto.TokenResponse;
import com.example.login.security.jwt.JwtTokenProvider;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider; //구현하셔야 할 JWT부분

    @Autowired
    public LoginController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // 1. AuthenticationManager를 통해 인증 시도
        //    - UsernamePasswordAuthenticationToken 객체로 인증 정보를 묶어 authenticationManager에 전달합니다.
        //    - 이 과정에서 CustomUserDetailsService가 호출되어 사용자 정보와 비밀번호를 검증합니다.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // 2. 인증이 성공하면 SecurityContextHolder에 인증 정보 저장
        //    - 이 정보는 현재 스레드 내에서 인증된 사용자 정보를 계속 유지할 수 있게 해줍니다.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. JWT 토큰 생성
        //    - 인증된 Authentication 객체를 기반으로 JWT 토큰을 생성합니다.
        String jwt = jwtTokenProvider.createToken(authentication);

        // 4. 생성된 JWT를 포함한 DTO를 응답으로 반환
        //    - Vue 클라이언트는 이 JSON 응답을 받아 토큰을 localStorage 등에 저장합니다.
        return ResponseEntity.ok(new TokenResponse("Bearer", jwt, jwtTokenProvider.getTokenExpirationInMilliSeconds()));
    }
}
