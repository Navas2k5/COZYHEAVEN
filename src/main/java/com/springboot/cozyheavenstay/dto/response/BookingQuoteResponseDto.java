package com.springboot.cozyheavenstay.dto.response;

import java.time.LocalDate;
import java.util.List;

public record BookingQuoteResponseDto (
        Long hotelId,
        LocalDate checkIn,
        LocalDate checkOut,
        Long nights,
        List<BookingRoomQuoteDto> rooms,
        Double totalAmount
){
}
