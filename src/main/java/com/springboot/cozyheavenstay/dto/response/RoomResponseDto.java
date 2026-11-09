package com.springboot.cozyheavenstay.dto.response;

import com.springboot.cozyheavenstay.enums.BedType;
import com.springboot.cozyheavenstay.enums.RoomStatus;

public record RoomResponseDto(
        Long id,
        String roomNumber,
        Double roomSize,
        BedType bedType,
        Integer maxOccuppancy,
        Double baseFare,
        boolean ac,
        RoomStatus roomStatus
) {
}
