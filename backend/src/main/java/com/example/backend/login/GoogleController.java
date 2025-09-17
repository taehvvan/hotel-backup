package com.example.backend.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.example.backend.login.dto.TokenResponse;
import com.example.backend.login.security.jwt.JwtTokenProvider;
import com.example.backend.register.GoogleService;
import com.example.backend.register.UserEntity;
import com.example.backend.register.UserService;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class GoogleController {

    @Autowired
    private GoogleService googleService;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Value("${google.auth.client-id}")
    private String googleClientId;

    @Value("${google.auth.redirect-uri}")
    private String googleRedirectUri;

    private final String FRONTEND_SUCCESS_REDIRECT_URL = "http://localhost:5173/login-success";
    private final String FRONTEND_FAILURE_REDIRECT_URL = "http://localhost:5173/login";
    
    @GetMapping("/google/login")
    public void redirectToGoogleLogin(HttpServletResponse response) throws IOException {
        String googleAuthUrl = "https://accounts.google.com/o/oauth2/v2/auth"
            + "?client_id=" + googleClientId
            + "&redirect_uri=" + googleRedirectUri
            + "&response_type=code"
            + "&scope=openid%20email%20profile";
        response.sendRedirect(googleAuthUrl);
    }

    @GetMapping("/google/callback")
    public RedirectView googleLogin(@RequestParam("code") String code) {
    try {
        UserEntity user = googleService.googleLoginOrRegister(code);
        String jwt = jwtTokenProvider.createToken(user.getEmail(), user.getRole());
        
        // ✅ 성공 시 리다이렉트 URL을 명확하게 변경
        return new RedirectView("http://localhost:5173/google/callback?token=" + jwt);
    } catch (Exception e) {
        System.err.println("Google 로그인 실패: " + e.getMessage());
        return new RedirectView(FRONTEND_FAILURE_REDIRECT_URL + "?error=google_failed");
    }
    }
}