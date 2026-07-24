package com.englishlms.gateway;

import com.englishlms.gateway.security.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static final String SECRET = "english-lms-jwt-secret-key-must-be-at-least-256-bits-long-for-security";
    private JwtUtil jwtUtil;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET);
        byte[] keyBytes = Decoders.BASE64.decode(
                java.util.Base64.getEncoder().encodeToString(SECRET.getBytes())
        );
        secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    @Test
    @DisplayName("Token hợp lệ - Validate thành công và trích xuất email & role")
    void validateToken_ValidToken_ReturnsTrue() {
        String token = Jwts.builder()
                .subject("student@gmail.com")
                .claim("role", "STUDENT")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(secretKey)
                .compact();

        assertTrue(jwtUtil.validateToken(token));
        assertEquals("student@gmail.com", jwtUtil.getEmailFromToken(token));
        assertEquals("STUDENT", jwtUtil.getRoleFromToken(token));
    }

    @Test
    @DisplayName("Token hết hạn - Validate thất bại")
    void validateToken_ExpiredToken_ReturnsFalse() {
        String expiredToken = Jwts.builder()
                .subject("student@gmail.com")
                .claim("role", "STUDENT")
                .issuedAt(new Date(System.currentTimeMillis() - 120000))
                .expiration(new Date(System.currentTimeMillis() - 60000))
                .signWith(secretKey)
                .compact();

        assertFalse(jwtUtil.validateToken(expiredToken));
    }

    @Test
    @DisplayName("Token sai hoặc định dạng không đúng - Validate thất bại")
    void validateToken_MalformedToken_ReturnsFalse() {
        assertFalse(jwtUtil.validateToken("invalid.jwt.token.string"));
    }
}
