package com.springboot.cozyheavenstay.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CustomerRequestDto(
        @NotBlank(message = "Name is required")
        @NotNull(message = "Name is required")
        String name,

        @NotBlank(message = "email is required")
        @NotNull(message = "email is required")
        @Email(message = "Email seems invalid")
        String email,

        @NotBlank(message = "contact is required")
        @NotNull(message = "contact is required")
        @Size(min = 10, max = 10, message = "contact should be 10 digit mobile number")
        String contact,

        @NotBlank(message = "gender is required")
        @NotNull(message = "gender is required")
        String gender,

        @NotBlank(message = "address is required")
        @NotNull(message = "address is required")
        String address,

        @NotNull(message = "Username is required")
        @NotBlank(message = "Username is required")
        String username,

        @NotNull(message = "Password is required")
        @NotBlank(message = "Password is required")
        String password
) {
}
