package com.springboot.cozyheavenstay.service;

import com.springboot.cozyheavenstay.dto.response.HotelRatingSummaryDto;
import com.springboot.cozyheavenstay.dto.response.PublicReviewResponseDto;
import com.springboot.cozyheavenstay.enums.HotelStatus;
import com.springboot.cozyheavenstay.enums.VerificationStatus;
import com.springboot.cozyheavenstay.mapper.ReviewMapper;
import com.springboot.cozyheavenstay.dto.request.ReviewRequestDto;
import com.springboot.cozyheavenstay.dto.response.ReviewResponseDto;
import com.springboot.cozyheavenstay.enums.BookingStatus;
import com.springboot.cozyheavenstay.exception.InvalidCallException;
import com.springboot.cozyheavenstay.exception.OwnerShipException;
import com.springboot.cozyheavenstay.exception.ResourceNotFoundException;
import com.springboot.cozyheavenstay.model.Bookings;
import com.springboot.cozyheavenstay.model.Hotels;
import com.springboot.cozyheavenstay.model.Reviews;
import com.springboot.cozyheavenstay.repository.BookingRepository;
import com.springboot.cozyheavenstay.repository.HotelRepository;
import com.springboot.cozyheavenstay.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;
    private final HotelRepository hotelRepository;

    @Transactional
    public ReviewResponseDto addReview(Long bookingId, ReviewRequestDto dto,String customerUsername){
        //find booking
        Bookings booking= bookingRepository.findById(bookingId).orElseThrow(()->
                new ResourceNotFoundException("Booking not found"));

        //check ownership
        if(!booking.getCustomer().getUser().getUsername().equals(customerUsername))
            throw new OwnerShipException("You can review only your own bookings");

        //allow review only for completed stays
        if(!booking.getBookingStatus().equals(BookingStatus.COMPLETED))
            throw new InvalidCallException("You can only review for completed bookings");

        //map request and attach booking
        Reviews review= ReviewMapper.convertDtoToEntity(dto);
        review.setBooking(booking);
        review.setCreatedAt(Instant.now());
        reviewRepository.save(review);

        return ReviewMapper.convertEntityToDto(review);


    }

    public List<PublicReviewResponseDto> getHotelReviews(Long hotelId, Integer page,Integer size){
        //validate page and size
        if(page<0||size<1||size>100)
            throw new InvalidCallException("Page should be atleast 1, size must be between 1 and 100");

        //get hotel
        Hotels hotel=hotelRepository.findById(hotelId).orElseThrow(()->
                new ResourceNotFoundException("Hotel not found"));

        //allow only publicly listed hotels
        if(hotel.getHotelStatus()!= HotelStatus.ACTIVE||hotel.getVerificationStatus()!= VerificationStatus.APPROVED)
            throw new ResourceNotFoundException("Hotel not found");

        //get reviews and pagination
        Pageable pageable= PageRequest.of(page,size);

        return reviewRepository.getReviewsByHotelId(hotelId,pageable);

    }

    public HotelRatingSummaryDto getHotelReviewSummary(Long hotelId){
        //get hotel
        Hotels hotel=hotelRepository.findById(hotelId).orElseThrow(()->
                new ResourceNotFoundException("Hotel not found"));

        //allow only publicly listed hotel
        if(hotel.getHotelStatus()!= HotelStatus.ACTIVE||hotel.getVerificationStatus()!= VerificationStatus.APPROVED)
            throw new ResourceNotFoundException("Hotel not found");

        //calculate average and count
        Double averageRating = reviewRepository.getAverageRatingByHotelId(hotelId);
        Long countOfReviews = reviewRepository.countReviewsByHotelId(hotelId);

        //round average to one decimal
        if(averageRating!=null)
            averageRating=Math.round(averageRating*10.0)/10.0;

        //return summary
        return new HotelRatingSummaryDto(hotelId,averageRating,countOfReviews);

    }

    public ReviewResponseDto getMyReview(Long bookingId, String customerUsername) {

        // Find booking
        Bookings booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Booking not found"));

        // Check ownership
        if (!booking.getCustomer().getUser().getUsername().equals(customerUsername))
            throw new OwnerShipException("You can view reviews only for your own bookings");

        // Find review
        Reviews review = reviewRepository.findByBookingId(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Review not found"));

        // Convert entity to DTO
        return ReviewMapper.convertEntityToDto(review);
    }
}
