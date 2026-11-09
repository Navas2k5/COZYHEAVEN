package com.springboot.cozyheavenstay.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminRequestDto(
        @NotBlank(message = "Name is required")
        @NotNull(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @NotNull(message = "Email is required")
        @Email(message = "Email seems invalid")
        String email,

        @NotNull(message = "Username is required")
        @NotBlank(message = "Username is required")
        String username,

        @NotNull(message = "Password is required")
        @NotBlank(message = "Password is required")
        String password
) {

}
