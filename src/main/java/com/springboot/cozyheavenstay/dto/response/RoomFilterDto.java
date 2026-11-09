package com.springboot.cozyheavenstay.dto.response;

import java.util.List;

public record RoomFilterDto(
        List<RoomResponseDto> list,
        PaginationDto paginationInfo
) {
}