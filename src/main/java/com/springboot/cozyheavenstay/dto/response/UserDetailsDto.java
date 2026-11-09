package com.springboot.cozyheavenstay.dto.response;

import com.springboot.cozyheavenstay.enums.Role;

public record UserDetailsDto(
        String username,
        Boolean isActive,
        Role role
) {
}
