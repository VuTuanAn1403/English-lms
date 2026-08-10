package com.englishlms.user.service;

import com.englishlms.user.dto.AdminCreateUserRequest;
import com.englishlms.user.dto.AdminResetPasswordRequest;
import com.englishlms.user.dto.AdminUpdateUserRequest;
import com.englishlms.user.dto.LoginRequest;
import com.englishlms.user.dto.LoginResponse;
import com.englishlms.user.dto.PageResponse;
import com.englishlms.user.dto.RegisterRequest;
import com.englishlms.user.dto.UpdateProfileRequest;
import com.englishlms.user.dto.UserResponse;
import com.englishlms.user.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    UserResponse getProfile(String email);

    UserResponse updateProfile(String email, UpdateProfileRequest request);

    long countUsers();

    List<UserResponse> getAllUsers();

    PageResponse<UserResponse> getUsers(int page, int size, String keyword, String role, String status, String sort);

    UserResponse getUserById(UUID id);

    UserResponse createUser(AdminCreateUserRequest request);

    UserResponse updateUser(UUID id, AdminUpdateUserRequest request);

    UserResponse updateUserStatus(UUID id, UserStatus status);

    UserResponse resetUserPassword(UUID id, AdminResetPasswordRequest request);

    void deleteUser(UUID id, String currentAdminEmail);
}
