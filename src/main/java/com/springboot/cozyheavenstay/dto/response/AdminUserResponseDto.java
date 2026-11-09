package com.springboot.cozyheavenstay.dto.response;

import com.springboot.cozyheavenstay.enums.Role;

public record AdminUserResponseDto(
        Long userId,
        String username,
        Role role,
        boolean active
) {
}
