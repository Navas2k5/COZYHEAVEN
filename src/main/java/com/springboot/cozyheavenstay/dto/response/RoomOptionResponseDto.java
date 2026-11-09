package com.springboot.cozyheavenstay.dto.response;

import com.springboot.cozyheavenstay.enums.BedType;

import java.util.List;

public record RoomOptionResponseDto(

        Double roomSize,

        BedType bedType,

        Integer maxOccupancy,

        Double baseFare,

        Boolean ac,

        Long availableCount,

        List<Long> availableRoomIds

) {
}