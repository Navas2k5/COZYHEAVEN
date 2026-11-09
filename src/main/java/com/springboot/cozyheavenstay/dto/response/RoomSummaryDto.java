package com.springboot.cozyheavenstay.dto.response;

public record RoomSummaryDto(
        Long totalRooms,
        Long activeRooms,
        Long inactiveRooms,
        Long maintenanceRooms
) {
}