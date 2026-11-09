package com.springboot.cozyheavenstay.dto.response;

import java.util.List;

public record FilterDto(
        List<CustomerHotelResponseDto> list,
        PaginationDto paginationInfo
) {
}