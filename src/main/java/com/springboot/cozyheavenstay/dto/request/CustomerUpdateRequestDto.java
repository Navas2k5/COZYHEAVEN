package com.springboot.cozyheavenstay.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerUpdateRequestDto(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email seems invalid")
        String email,

        @NotBlank(message = "Contact is required")
        @Size(min = 10, max = 10, message = "Contact should be 10 digit mobile number")
        String contact,

        @NotBlank(message = "Gender is required")
        String gender,

        @NotBlank(message = "Address is required")
        String address

) {
}