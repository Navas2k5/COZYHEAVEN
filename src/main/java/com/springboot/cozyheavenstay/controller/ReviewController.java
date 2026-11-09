package com.springboot.cozyheavenstay.controller;

import com.springboot.cozyheavenstay.dto.request.ReviewRequestDto;
import com.springboot.cozyheavenstay.dto.response.HotelRatingSummaryDto;
import com.springboot.cozyheavenstay.dto.response.PublicReviewResponseDto;
import com.springboot.cozyheavenstay.dto.response.ReviewResponseDto;
import com.springboot.cozyheavenstay.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/api/review/booking/{bookingId}/add")
    public ReviewResponseDto addReview(@PathVariable Long bookingId,
                                       @Valid @RequestBody ReviewRequestDto reviewRequestDto,
                                       Principal principal){
        return reviewService.addReview(bookingId,reviewRequestDto, principal.getName());
    }

    @GetMapping("/api/review/hotel/{hotelId}/all")
    public List<PublicReviewResponseDto> getReviewsByHotel(@PathVariable Long hotelId,
                                                           @RequestParam(name = "page",required = false,defaultValue = "0")Integer page,
                                                           @RequestParam(name = "size",required = false,defaultValue = "20")Integer size){
        return reviewService.getHotelReviews(hotelId, page, size);
    }

    @GetMapping("/api/review/hotel/{hotelId}/summary")
    public HotelRatingSummaryDto getHotelSummary(@PathVariable Long hotelId){
        return reviewService.getHotelReviewSummary(hotelId);
    }

    @GetMapping("/api/review/booking/{bookingId}/my")
    public ReviewResponseDto getMyReview(
            @PathVariable Long bookingId,
            Principal principal) {
        return reviewService.getMyReview(bookingId, principal.getName());
    }
}
