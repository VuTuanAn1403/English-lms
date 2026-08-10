package com.englishlms.user.controller;

import com.englishlms.user.dto.AdminCreateUserRequest;
import com.englishlms.user.dto.AdminResetPasswordRequest;
import com.englishlms.user.dto.AdminUpdateUserRequest;
import com.englishlms.user.dto.ApiResponse;
import com.englishlms.user.dto.PageResponse;
import com.englishlms.user.dto.UpdateProfileRequest;
import com.englishlms.user.dto.UserResponse;
import com.englishlms.user.dto.UserStatusRequest;
import com.englishlms.user.entity.UserStatus;
import com.englishlms.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Quản lý Hồ sơ & Tài khoản Người dùng")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @Operation(summary = "Lấy thông tin hồ sơ cá nhân", description = "Xem thông tin tài khoản của người dùng đang đăng nhập dựa theo JWT token.")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponse response = userService.getProfile(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin hồ sơ thành công", response));
    }

    @PutMapping("/profile")
    @Operation(summary = "Cập nhật thông tin hồ sơ cá nhân", description = "Cập nhật các thông tin như họ tên, avatar của người dùng đang đăng nhập.")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        UserResponse response = userService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật hồ sơ thành công", response));
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Đếm tổng số người dùng thực trong hệ thống", description = "Trả về số lượng tài khoản người dùng thực tế từ cơ sở dữ liệu.")
    public ResponseEntity<ApiResponse<Long>> countUsers() {
        long count = userService.countUsers();
        return ResponseEntity.ok(ApiResponse.success("Lấy số lượng người dùng thành công", count));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin lấy danh sách người dùng phân trang", description = "Hỗ trợ phân trang, tìm kiếm theo keyword (tên, email), lọc theo vai trò (ADMIN, STUDENT) và trạng thái (ACTIVE, INACTIVE).")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        PageResponse<UserResponse> response = userService.getUsers(page, size, keyword, role, status, sort);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách người dùng thành công", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xem chi tiết tài khoản người dùng theo ID", description = "Lấy đầy đủ thông tin một tài khoản người dùng.")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin người dùng thành công", response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin tạo tài khoản người dùng mới", description = "Tạo mới tài khoản người dùng (Admin hoặc Học viên). Kiểm tra trùng email và mã hóa mật khẩu BCrypt.")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody AdminCreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Tạo người dùng mới thành công", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin cập nhật thông tin tài khoản người dùng", description = "Cập nhật tên, email, vai trò, avatar và trạng thái hoạt động của tài khoản.")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody AdminUpdateUserRequest request
    ) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin người dùng thành công", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin Khóa hoặc Mở khóa tài khoản người dùng", description = "Chuyển đổi trạng thái tài khoản giữa ACTIVE và INACTIVE. Tài khoản INACTIVE sẽ không thể đăng nhập.")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UserStatusRequest request
    ) {
        UserResponse response = userService.updateUserStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái người dùng thành công", response));
    }

    @PatchMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin Đặt lại mật khẩu tài khoản người dùng", description = "Đặt lại mật khẩu mới cho người dùng và mã hóa BCrypt.")
    public ResponseEntity<ApiResponse<UserResponse>> resetUserPassword(
            @PathVariable UUID id,
            @Valid @RequestBody AdminResetPasswordRequest request
    ) {
        UserResponse response = userService.resetUserPassword(id, request);
        return ResponseEntity.ok(ApiResponse.success("Đặt lại mật khẩu thành công", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin xóa tài khoản người dùng", description = "Xóa tài khoản khỏi hệ thống. Không được phép xóa chính tài khoản Admin đang đăng nhập.")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable UUID id,
            Principal principal
    ) {
        String currentAdminEmail = principal != null ? principal.getName() : "admin@gmail.com";
        userService.deleteUser(id, currentAdminEmail);
        return ResponseEntity.ok(ApiResponse.success("Xóa tài khoản người dùng thành công", null));
    }
}
