package com.springboot.cozyheavenstay.dto.response;

import com.springboot.cozyheavenstay.enums.BookingStatus;

import java.time.Instant;
import java.time.LocalDate;

public record BookingResponseDto(
        Long bookingId,
        LocalDate checkIn,
        LocalDate checkOut,
        Double totalAmount,
        BookingStatus bookingStatus,
        Instant expiresAt
) {
}
