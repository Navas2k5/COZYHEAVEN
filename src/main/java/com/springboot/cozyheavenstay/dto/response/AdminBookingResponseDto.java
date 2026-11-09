package com.springboot.cozyheavenstay.dto.response;

import com.springboot.cozyheavenstay.enums.BookingStatus;
import com.springboot.cozyheavenstay.enums.PaymentStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record AdminBookingResponseDto(
        Long bookingId,
        Long hotelId,
        String hotelName,
        String customerName,
        LocalDate checkIn,
        LocalDate checkOut,
        Instant bookedAt,
        BookingStatus bookingStatus,
        Double totalAmount,
        List<BookingRoomDetailsDto> rooms,
        PaymentStatus paymentStatus,
        Double refundAmount,
        Instant refundedAt
) {
}
