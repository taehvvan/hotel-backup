package com.example.backend.login;

import com.example.backend.login.dto.LoginRequest;
import com.example.backend.login.dto.TokenResponse;
import com.example.backend.login.security.jwt.JwtTokenProvider;
import com.example.backend.login.security.jwt.LoginDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Autowired
    public LoginController(@Lazy AuthenticationManager authenticationManager,
     JwtTokenProvider jwtTokenProvider,
     UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
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
        String jwt = jwtTokenProvider.createToken(authentication);
        return ResponseEntity.ok(new TokenResponse("Bearer", jwt, jwtTokenProvider.getTokenExpirationInMilliSeconds()));
    }

    @GetMapping("/me")
    public ResponseEntity<LoginDTO> getCurrentUser(@RequestHeader("Authorization") String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");
        String email = jwtTokenProvider.getUsernameFromToken(token);
        UserEntity user = userRepository.findByEmail(email);
        LoginDTO dto = new LoginDTO(user.getId(), user.getName(), user.getEmail());
        return ResponseEntity.ok(dto);
    }
}

