package com.springboot.cozyheavenstay.dto.response;

public record PaginationDto(
        int totalPages,
        Long totalRecords,
        Integer numRecordsInPage
) {
}