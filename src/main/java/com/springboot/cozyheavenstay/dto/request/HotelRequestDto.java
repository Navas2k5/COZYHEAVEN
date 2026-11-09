package com.springboot.cozyheavenstay.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HotelRequestDto(
        @NotBlank(message = "Name is required")
        @NotNull(message = "Name is required")
        String hotelName,

        @NotBlank(message = "Description is required")
        @NotNull(message = "Description is required")
        String description,

        @NotBlank(message = "Location is required")
        @NotNull(message = "Location is required")
        String location,

        @NotBlank(message = "Address is required")
        @NotNull(message = "Address is required")
        String address,

        @NotBlank(message = "Amenities is required")
        @NotNull(message = "Amenities is required")
        String amenities

) {
}
