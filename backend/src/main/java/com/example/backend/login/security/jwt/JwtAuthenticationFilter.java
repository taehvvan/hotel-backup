package com.example.backend.login.security.jwt;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 로그인, 회원가입 요청은 JWT 검사하지 않음
        if (path.startsWith("/api/auth/login") || path.startsWith("/api/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1. 요청 헤더에서 JWT 토큰 추출
        String token = resolveToken(request);

        // 2. 토큰 유효성 검증
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 3. 토큰이 유효하면 인증 객체(Authentication) 생성
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            // 4. SecurityContext에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    // 요청 헤더에서 토큰을 추출하는 헬퍼 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
