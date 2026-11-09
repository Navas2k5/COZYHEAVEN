package com.springboot.cozyheavenstay.dto.response;

import java.util.List;

public record OwnerBookingFilterDto(
        List<OwnerBookingResponseDto> list,
        PaginationDto paginationInfo
) {
}