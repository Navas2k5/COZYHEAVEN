package com.springboot.cozyheavenstay.dto.request;

import jakarta.validation.constraints.*;

public record ReviewRequestDto(
        @NotNull(message = "Rating is required")
        @Min(value = 1,message = "Rating must be at least 1")
        @Max(value = 5,message = "Rating cannot exceed 5")
        Integer rating,

        @NotBlank(message = "Comment is required")
        @Size(max = 255,message = "Comments cannot exceed 255 characters")
        String comment
) {
}
