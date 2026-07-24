package com.englishlms.user.dto;

import com.englishlms.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private UUID id;
    private String fullName;
    private String email;
    private Role role;
    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
