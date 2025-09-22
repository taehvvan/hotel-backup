package com.example.backend;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/*") // 모든 /api 경로에 대해 CORS 허용
                .allowedOrigins("http://localhost:5173", "http://localhost:8888", "http://172.16.15.93:5173",
                        "http://172.16.15.95:5173") // Vue.js 앱의 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true); // 자격증명(쿠키 등) 포함한 요청 허용ㄴ
    }
}
