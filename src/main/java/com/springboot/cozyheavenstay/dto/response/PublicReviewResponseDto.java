package com.springboot.cozyheavenstay.dto.response;

import java.time.Instant;

public record PublicReviewResponseDto(
        String customerName,
        Integer rating,
        String comment,
        Instant createdAt
) {
}
