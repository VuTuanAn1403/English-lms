package com.englishlms.course.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseSecurityTest {

    @Mock
    private JwtService jwtService;

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private static final String SECRET = "english-lms-jwt-secret-key-must-be-at-least-256-bits-long-for-security";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService);
    }

    @Test
    @DisplayName("Request gửi Header ADMIN giả mạo (X-User-Role: ADMIN) không có JWT - Không được gán Authentication")
    void doFilterInternal_FakeAdminHeader_NoAuthenticationSet() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Email", "attacker@gmail.com");
        request.addHeader("X-User-Role", "ADMIN");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Request chứa Bearer Token STUDENT hợp lệ - Được gán ROLE_STUDENT")
    void doFilterInternal_ValidStudentToken_SetsStudentRole() throws Exception {
        String token = "valid_student_token";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        when(jwtService.validateToken(token)).thenReturn(true);
        when(jwtService.getEmailFromToken(token)).thenReturn("student@gmail.com");
        when(jwtService.getRoleFromToken(token)).thenReturn("STUDENT");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("student@gmail.com", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT")));
    }

    @Test
    @DisplayName("Request chứa Bearer Token ADMIN hợp lệ - Được gán ROLE_ADMIN")
    void doFilterInternal_ValidAdminToken_SetsAdminRole() throws Exception {
        String token = "valid_admin_token";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        when(jwtService.validateToken(token)).thenReturn(true);
        when(jwtService.getEmailFromToken(token)).thenReturn("admin@gmail.com");
        when(jwtService.getRoleFromToken(token)).thenReturn("ADMIN");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("admin@gmail.com", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }
}
