package com.springboot.cozyheavenstay.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record BookingQuoteRequestDto (

        @NotNull(message = "Check-in is required")
        LocalDate checkIn,

        @NotNull(message = "Check-out is required")
        LocalDate checkOut,

        @NotNull(message = "Select at least one room")
        List<@NotNull @Valid  BookingRoomRequestDto> rooms
){
}
