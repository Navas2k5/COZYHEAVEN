package com.springboot.cozyheavenstay.dto.request;

import com.springboot.cozyheavenstay.enums.BedType;
import jakarta.validation.constraints.*;

public record RoomRequestDto(
        @NotBlank(message = "Room number is required")
        @NotNull(message = "Room number is required")
        String roomNumber,

        @NotNull(message = "Room size is required")
        @Positive(message = "Room size must be greater than 0")
        Double roomSize,

        @NotNull(message ="Bed type is required")
        BedType bedType,

        @NotNull(message = "Maximum occupancy is required")
        @Positive(message = "Maximum occupancy must be greater than 0")
        @Min(value = 1,message = "Maximum Occupancy must be atlease one")
        @Max(value = 6,message = "Maximum Occupancy cannot exceed six")
        Integer maxOccupancy,

        @NotNull(message ="Base fare is required")
        Double baseFare,

        @NotNull(message ="Base fare is required")
        Boolean ac
) {
}
