package com.springboot.cozyheavenstay.dto.response;

public record CustomerHotelResponseDto(
        Long id,
        String hotelName,
        String description,
        String location,
        String address,
        String amenities,
        String imageUrl
) {
}
