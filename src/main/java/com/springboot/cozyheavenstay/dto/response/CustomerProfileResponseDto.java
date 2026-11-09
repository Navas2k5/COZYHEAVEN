package com.springboot.cozyheavenstay.dto.response;

public record CustomerProfileResponseDto(
        Long customerId,
        String name,
        String email,
        String contact,
        String gender,
        String address,
        String username
) {
}