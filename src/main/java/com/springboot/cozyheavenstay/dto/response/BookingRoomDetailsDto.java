package com.springboot.cozyheavenstay.dto.response;

public record BookingRoomDetailsDto(
        Long roomId,
        String roomNumber,
        Integer adults,
        Integer children,
        Double pricePerNight,
        Double subTotal
) {
}
