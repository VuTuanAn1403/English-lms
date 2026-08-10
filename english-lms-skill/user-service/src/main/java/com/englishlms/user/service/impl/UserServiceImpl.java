package com.englishlms.user.service.impl;

import com.englishlms.user.dto.AdminCreateUserRequest;
import com.englishlms.user.dto.AdminResetPasswordRequest;
import com.englishlms.user.dto.AdminUpdateUserRequest;
import com.englishlms.user.dto.LoginRequest;
import com.englishlms.user.dto.LoginResponse;
import com.englishlms.user.dto.PageResponse;
import com.englishlms.user.dto.RegisterRequest;
import com.englishlms.user.dto.UpdateProfileRequest;
import com.englishlms.user.dto.UserResponse;
import com.englishlms.user.entity.Role;
import com.englishlms.user.entity.User;
import com.englishlms.user.entity.UserStatus;
import com.englishlms.user.exception.EmailAlreadyExistsException;
import com.englishlms.user.exception.ResourceNotFoundException;
import com.englishlms.user.exception.UnauthorizedException;
import com.englishlms.user.mapper.UserMapper;
import com.englishlms.user.repository.UserRepository;
import com.englishlms.user.security.JwtService;
import com.englishlms.user.service.UserService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserServiceImpl(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.STUDENT);
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);
        return userMapper.toUserResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Email hoặc mật khẩu không chính xác"));

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new UnauthorizedException("Tài khoản của bạn đã bị khóa. Vui lòng liên hệ Quản trị viên.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Email hoặc mật khẩu không chính xác");
        }

        String accessToken = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return LoginResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(userMapper.toUserResponse(user))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng có email: " + email));
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng có email: " + email));

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toUserResponse(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUsers() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getUsers(int page, int size, String keyword, String role, String status, String sort) {
        Sort sortObj = Sort.by(Sort.Direction.DESC, "createdAt");
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            if (parts.length == 2) {
                Sort.Direction dir = "asc".equalsIgnoreCase(parts[1]) ? Sort.Direction.ASC : Sort.Direction.DESC;
                sortObj = Sort.by(dir, parts[0]);
            } else {
                sortObj = Sort.by(Sort.Direction.DESC, parts[0]);
            }
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);

        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String k = "%" + keyword.trim().toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("fullName")), k);
                Predicate emailLike = cb.like(cb.lower(root.get("email")), k);
                predicates.add(cb.or(nameLike, emailLike));
            }

            if (role != null && !role.isBlank() && !"ALL".equalsIgnoreCase(role)) {
                try {
                    Role roleEnum = Role.valueOf(role.toUpperCase());
                    predicates.add(cb.equal(root.get("role"), roleEnum));
                } catch (IllegalArgumentException ignored) {}
            }

            if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
                try {
                    UserStatus statusEnum = UserStatus.valueOf(status.toUpperCase());
                    predicates.add(cb.equal(root.get("status"), statusEnum));
                } catch (IllegalArgumentException ignored) {}
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<User> userPage = userRepository.findAll(spec, pageable);

        List<UserResponse> items = userPage.getContent().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());

        return PageResponse.<UserResponse>builder()
                .items(items)
                .pageNumber(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .isLast(userPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với id: " + id));
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse createUser(AdminCreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : Role.STUDENT);
        user.setStatus(UserStatus.ACTIVE);

        if (request.getAvatar() != null && !request.getAvatar().isBlank()) {
            user.setAvatar(request.getAvatar());
        } else {
            user.setAvatar("https://ui-avatars.com/api/?name=" + request.getFullName().replace(" ", "+"));
        }

        User savedUser = userRepository.save(user);
        return userMapper.toUserResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(UUID id, AdminUpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với id: " + id));

        if (request.getEmail() != null && !request.getEmail().isBlank() && !request.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new EmailAlreadyExistsException(request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        if (request.getAvatar() != null && !request.getAvatar().isBlank()) {
            user.setAvatar(request.getAvatar());
        }

        User updated = userRepository.save(user);
        return userMapper.toUserResponse(updated);
    }

    @Override
    public UserResponse updateUserStatus(UUID id, UserStatus status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với id: " + id));
        user.setStatus(status);
        User updated = userRepository.save(user);
        return userMapper.toUserResponse(updated);
    }

    @Override
    public UserResponse resetUserPassword(UUID id, AdminResetPasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với id: " + id));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        User updated = userRepository.save(user);
        return userMapper.toUserResponse(updated);
    }

    @Override
    public void deleteUser(UUID id, String currentAdminEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với id: " + id));

        if (user.getEmail().equalsIgnoreCase(currentAdminEmail)) {
            throw new IllegalArgumentException("Không thể xóa chính tài khoản Admin đang đăng nhập!");
        }

        userRepository.delete(user);
    }
}
