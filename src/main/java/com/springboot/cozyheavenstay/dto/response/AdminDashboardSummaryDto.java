package com.springboot.cozyheavenstay.dto.response;

public record AdminDashboardSummaryDto(
        Long totalHotels,
        Long pendingHotels,
        Long approvedHotels,
        Long rejectedHotels,
        Long activeHotels,
        Long inactiveHotels,
        Long totalBookings,
        Long totalUsers,
        Long totalCustomers,
        Long totalOwners
) {}