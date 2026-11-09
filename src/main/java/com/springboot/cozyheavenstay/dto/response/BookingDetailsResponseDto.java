package com.springboot.cozyheavenstay.dto.response;

import com.springboot.cozyheavenstay.enums.BookingStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record BookingDetailsResponseDto(
        Long bookingId,
        Long hotelId,
        String hotelName,
        String address,
        LocalDate checkIn,
        LocalDate checkOut,
        Long nights,
        Double totalAmount,
        BookingStatus bookingStatus,
        Instant bookedAt,
        Instant expiresAt,
        List<BookingRoomDetailsDto> rooms,
        PaymentResponseDto payment
) {
}
