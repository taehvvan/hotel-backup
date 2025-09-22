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

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {

        System.out.println("JwtAuthenticationFilter 실행됨");
        String path = request.getRequestURI();

        // 로그인, 회원가입 요청은 JWT 검사하지 않음
        if (path.startsWith("/api/auth/login") || path.startsWith("/api/register") ||
            path.startsWith("/api/send-code") || path.startsWith("/api/verify-code") || 
            path.startsWith("/api/check-email") || path.startsWith("/api/search") || 
            path.startsWith("/api/detail")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            System.out.println("Request URI: " + request.getRequestURI());
            System.out.println("Token: " + resolveToken(request));
            // 요청 헤더에서 토큰 추출
            String token = resolveToken(request);

            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 토큰이 유효하면 인증 객체 생성 후 SecurityContext에 저장
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            
                System.out.println("Principal: " + authentication.getPrincipal());
                System.out.println("Authorities: " + authentication.getAuthorities());
            } else {
                // 토큰 없거나 유효하지 않으면 401 반환
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing JWT token");
                return;
            }

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Failed to authenticate JWT token: " + e.getMessage());
            return;
        }

        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    // 요청 헤더에서 Bearer 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
