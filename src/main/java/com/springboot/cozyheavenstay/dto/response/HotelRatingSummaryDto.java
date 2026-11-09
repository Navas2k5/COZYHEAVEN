package com.springboot.cozyheavenstay.dto.response;

public record HotelRatingSummaryDto(
        Long hotelId,
        Double averageRating,
        Long totalReviews
) {
}
