package com.springboot.cozyheavenstay.dto.response;

import com.springboot.cozyheavenstay.enums.BookingStatus;
import com.springboot.cozyheavenstay.enums.PaymentStatus;

import java.time.Instant;

public record PaymentResponseDto (
        Long paymentId,
        Long bookingId,
        Double amount,
        PaymentStatus paymentStatus,
        BookingStatus bookingStatus,
        Instant paidAt
){
}
