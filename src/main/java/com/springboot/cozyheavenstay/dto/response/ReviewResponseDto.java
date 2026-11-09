package com.springboot.cozyheavenstay.dto.response;

import java.time.Instant;

public record ReviewResponseDto(
        Long reviewId,
        Long bookingId,
        String customerName,
        Integer rating,
        String comment,
        Instant createdAt
) {
}
