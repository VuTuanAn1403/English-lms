package com.englishlms.user.controller;

import com.englishlms.user.dto.LoginRequest;
import com.englishlms.user.dto.LoginResponse;
import com.englishlms.user.dto.RegisterRequest;
import com.englishlms.user.dto.UserResponse;
import com.englishlms.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Xác thực người dùng & Quản lý phiên đăng nhập")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản người dùng mới", description = "Tạo tài khoản học viên mới với email và mật khẩu.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Đăng ký tài khoản thành công",
                    content = @Content(schema = @Schema(implementation = com.englishlms.user.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ hoặc Email đã tồn tại",
                    content = @Content(schema = @Schema(implementation = com.englishlms.user.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống nội bộ",
                    content = @Content(schema = @Schema(implementation = com.englishlms.user.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.englishlms.user.dto.ApiResponse<UserResponse>> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Thông tin đăng ký tài khoản")
            @Valid @RequestBody RegisterRequest request
    ) {
        UserResponse response = userService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(com.englishlms.user.dto.ApiResponse.success("Đăng ký tài khoản thành công", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập hệ thống", description = "Xác thực email và mật khẩu để lấy JWT Bearer Access Token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đăng nhập thành công",
                    content = @Content(schema = @Schema(implementation = com.englishlms.user.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ",
                    content = @Content(schema = @Schema(implementation = com.englishlms.user.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Sai email hoặc mật khẩu",
                    content = @Content(schema = @Schema(implementation = com.englishlms.user.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống nội bộ",
                    content = @Content(schema = @Schema(implementation = com.englishlms.user.dto.ApiResponse.class)))
    })
    public ResponseEntity<com.englishlms.user.dto.ApiResponse<LoginResponse>> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Thông tin đăng nhập")
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(com.englishlms.user.dto.ApiResponse.success("Đăng nhập thành công", response));
    }
}


