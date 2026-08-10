package com.englishlms.user.security;

import com.englishlms.user.dto.LoginRequest;
import com.englishlms.user.dto.LoginResponse;
import com.englishlms.user.dto.UserResponse;
import com.englishlms.user.entity.Role;
import com.englishlms.user.entity.User;
import com.englishlms.user.entity.UserStatus;
import com.englishlms.user.mapper.UserMapper;
import com.englishlms.user.repository.UserRepository;
import com.englishlms.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSecurityTest {

    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UUID expectedUserId;
    private static final String SECRET = "english-lms-jwt-secret-key-must-be-at-least-256-bits-long-for-security";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 86400000, 604800000);

        expectedUserId = UUID.randomUUID();
        testUser = User.builder()
                .id(expectedUserId)
                .fullName("Test Student")
                .email("student_test@gmail.com")
                .password("encoded_pass")
                .role(Role.STUDENT)
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("JWT Token phải chứa userId thực từ database và role STUDENT")
    void generateToken_ContainsRealUserIdAndRole() {
        String token = jwtService.generateToken(testUser.getId(), testUser.getEmail(), testUser.getRole().name());

        assertTrue(jwtService.validateToken(token));
        assertEquals("student_test@gmail.com", jwtService.getEmailFromToken(token));
        assertEquals("STUDENT", jwtService.getRoleFromToken(token));
        assertEquals(expectedUserId, jwtService.getUserIdFromToken(token));
    }

    @Test
    @DisplayName("Đăng nhập thành công tạo JWT Token chứa userId thực")
    void login_ReturnsJwtWithRealUserId() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("student_test@gmail.com")
                .password("123456")
                .build();

        when(userRepository.findByEmail("student_test@gmail.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("123456", "encoded_pass")).thenReturn(true);
        when(userMapper.toUserResponse(any())).thenReturn(new UserResponse());

        // Re-inject Real JwtService into UserServiceImpl
        UserServiceImpl realJwtUserService = new UserServiceImpl(userRepository, userMapper, passwordEncoder, jwtService);

        LoginResponse response = realJwtUserService.login(loginRequest);

        assertNotNull(response.getToken());
        UUID userIdFromToken = jwtService.getUserIdFromToken(response.getToken());
        assertEquals(expectedUserId, userIdFromToken);
        assertEquals("student_test@gmail.com", jwtService.getEmailFromToken(response.getToken()));
        assertEquals("STUDENT", jwtService.getRoleFromToken(response.getToken()));
    }
}
