package com.englishlms.course.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private final SecretKey secretKey;

    public JwtService(
            @Value("${jwt.secret:${JWT_SECRET:english-lms-jwt-secret-key-must-be-at-least-256-bits-long-for-security}}") String secret
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(
                java.util.Base64.getEncoder().encodeToString(secret.getBytes())
        );
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    public java.util.UUID getUserIdFromToken(String token) {
        Object userIdObj = getClaims(token).get("userId");
        if (userIdObj != null) {
            try {
                return java.util.UUID.fromString(userIdObj.toString());
            } catch (Exception ignored) {}
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            log.warn("Invalid JWT token in Course Service: {}", e.getMessage());
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
