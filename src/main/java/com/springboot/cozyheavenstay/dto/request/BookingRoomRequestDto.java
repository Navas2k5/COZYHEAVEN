package com.springboot.cozyheavenstay.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BookingRoomRequestDto (
        @NotNull(message = "Room ID is required")
        @Positive
        Long roomId,

        @NotNull(message = "Adult count is required")
        @Min(value = 1,message = "At least one dult is required per room")
        Integer adults,

        @NotNull(message = "Child count is required")
        @Min(value = 0,message = "Child count cannot be negative")
        Integer children

){
}
