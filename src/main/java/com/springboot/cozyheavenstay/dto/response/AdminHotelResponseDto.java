package com.springboot.cozyheavenstay.dto.response;

import com.springboot.cozyheavenstay.enums.HotelStatus;
import com.springboot.cozyheavenstay.enums.VerificationStatus;

import java.time.Instant;

public record AdminHotelResponseDto(
        Long id,
        String hotelName,
        String description,
        String location,
        String address,
        String amenities,
        String imageUrl,
        HotelStatus hotelStatus,
        VerificationStatus verificationStatus,
        Instant createdAt,
        Long ownerId,
        String ownerName,
        String businessName,
        String ownerEmail,
        String ownerContact
) {
}
