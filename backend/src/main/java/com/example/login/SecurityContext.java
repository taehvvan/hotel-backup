package com.example.login;

import com.example.login.security.jwt.JwtAuthenticationFilter;
import com.example.login.security.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityContext {

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityContext(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (REST API는 필요 없음)
            .httpBasic(httpBasic -> httpBasic.disable()) // HTTP Basic 인증 비활성화
            .formLogin(formLogin -> formLogin.disable()) // 폼 로그인 비활성화

            // 세션 사용하지 않음 (STATELESS)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(authz -> authz
                // --- 로그인 없이 접근 가능한 API 및 정적 리소스 ---
                .requestMatchers(
                    "/api/auth/login",  // 로그인 API
                    "/api/auth/signup", // 회원가입 API
                    "/", "/search",
                    "/hotel/**", "/landmark/**", "/heritage/**",
                    "/terms", "/privacy",
                    "/accommodations", "/landmarks", "/heritage",
                    "/checkout-guest",
                    "/images/**", "/css/**", "/js/**"
                ).permitAll()

                // --- 역할 기반 API 접근 제어 (API 경로로 변경) ---
                .requestMatchers(
                    "/api/user/**", "/api/mypage", "/api/wishlist"
                ).hasRole("USER")

                .requestMatchers(
                    "/api/manager/**"
                ).hasRole("MANAGER")

                .requestMatchers(
                    "/api/admin/**"
                ).hasRole("ADMIN")

                // 그 외 나머지 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            
            // JWT 인증을 위한 커스텀 필터 추가
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}