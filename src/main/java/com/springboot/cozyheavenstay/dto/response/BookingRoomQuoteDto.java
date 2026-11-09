package com.springboot.cozyheavenstay.dto.response;

public record BookingRoomQuoteDto (
        Long roomId,
        String roomNumber,
        Integer adults,
        Integer children,
        Double baseFare,
        Double extraChargesPerNight,
        Double pricePerNight,
        Double subTotal
){
}
