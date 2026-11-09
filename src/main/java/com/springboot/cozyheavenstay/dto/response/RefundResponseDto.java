package com.springboot.cozyheavenstay.dto.response;

import com.springboot.cozyheavenstay.enums.BookingStatus;
import com.springboot.cozyheavenstay.enums.PaymentStatus;

import java.time.Instant;

public record RefundResponseDto(
        Long paymentId,
        Long bookingId,
        Double refundAmount,
        PaymentStatus paymentStatus,
        BookingStatus bookingStatus,
        Instant refundedAt
) {
}
