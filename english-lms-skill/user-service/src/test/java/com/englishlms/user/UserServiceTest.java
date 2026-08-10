package com.englishlms.user;

import com.englishlms.user.dto.LoginRequest;
import com.englishlms.user.dto.LoginResponse;
import com.englishlms.user.dto.RegisterRequest;
import com.englishlms.user.dto.UserResponse;
import com.englishlms.user.entity.Role;
import com.englishlms.user.entity.User;
import com.englishlms.user.exception.EmailAlreadyExistsException;
import com.englishlms.user.exception.UnauthorizedException;
import com.englishlms.user.mapper.UserMapper;
import com.englishlms.user.repository.UserRepository;
import com.englishlms.user.security.JwtService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .fullName("Nguyễn Văn A")
                .email("student@gmail.com")
                .password("123456")
                .build();

        loginRequest = LoginRequest.builder()
                .email("student@gmail.com")
                .password("123456")
                .build();

        user = User.builder()
                .id(UUID.randomUUID())
                .fullName("Nguyễn Văn A")
                .email("student@gmail.com")
                .password("encoded_password")
                .role(Role.STUDENT)
                .build();

        userResponse = UserResponse.builder()
                .id(user.getId())
                .fullName("Nguyễn Văn A")
                .email("student@gmail.com")
                .role(Role.STUDENT)
                .build();
    }

    @Test
    @DisplayName("Đăng ký thành công - Trả về thông tin người dùng")
    void register_Success() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userMapper.toUser(registerRequest)).thenReturn(user);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.register(registerRequest);

        assertNotNull(result);
        assertEquals("student@gmail.com", result.getEmail());
        assertEquals("Nguyễn Văn A", result.getFullName());
        assertEquals(Role.STUDENT, result.getRole());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Đăng ký thất bại - Email đã tồn tại")
    void register_EmailAlreadyExists_ThrowsException() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Đăng nhập thành công - Trả về JWT Token")
    void login_Success() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name())).thenReturn("mocked_access_token");
        when(jwtService.generateRefreshToken(user.getEmail())).thenReturn("mocked_refresh_token");
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        LoginResponse response = userService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mocked_access_token", response.getToken());
        assertEquals("mocked_refresh_token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertNotNull(response.getUser());

        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
    }

    @Test
    @DisplayName("Đăng nhập thất bại - Sai mật khẩu")
    void login_WrongPassword_ThrowsException() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> userService.login(loginRequest));
    }
}
