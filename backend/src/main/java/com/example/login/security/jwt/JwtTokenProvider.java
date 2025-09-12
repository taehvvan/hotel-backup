package com.example.login.security.jwt;

import org.springframework.security.core.Authentication;

public class JwtTokenProvider {

    public String createToken(Authentication authentication) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createToken'");
    }

    public Long getTokenExpirationInMilliSeconds() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTokenExpirationInMilliSeconds'");
    }

    public boolean validateToken(String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validateToken'");
    }

    public Authentication getAuthentication(String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAuthentication'");
    }

}
