package com.springboot.cozyheavenstay.service;

import com.springboot.cozyheavenstay.dto.request.ReviewRequestDto;
import com.springboot.cozyheavenstay.dto.response.HotelRatingSummaryDto;
import com.springboot.cozyheavenstay.dto.response.ReviewResponseDto;
import com.springboot.cozyheavenstay.enums.BookingStatus;
import com.springboot.cozyheavenstay.enums.HotelStatus;
import com.springboot.cozyheavenstay.enums.VerificationStatus;
import com.springboot.cozyheavenstay.exception.InvalidCallException;
import com.springboot.cozyheavenstay.exception.OwnerShipException;
import com.springboot.cozyheavenstay.exception.ResourceNotFoundException;
import com.springboot.cozyheavenstay.model.Bookings;
import com.springboot.cozyheavenstay.model.Customer;
import com.springboot.cozyheavenstay.model.Hotels;
import com.springboot.cozyheavenstay.model.Reviews;
import com.springboot.cozyheavenstay.model.User;
import com.springboot.cozyheavenstay.repository.BookingRepository;
import com.springboot.cozyheavenstay.repository.HotelRepository;
import com.springboot.cozyheavenstay.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
    @InjectMocks
    private ReviewService reviewService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private HotelRepository hotelRepository;

    @Test
    public void addReviewTest(){
        User user = new User();
        user.setUsername("testcustomer");

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Test Customer");
        customer.setUser(user);

        Bookings booking = new Bookings();
        booking.setId(1L);
        booking.setCustomer(customer);
        booking.setBookingStatus(BookingStatus.COMPLETED);

        ReviewRequestDto dto = new ReviewRequestDto(5,"Excellent stay");

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(reviewRepository.save(any(Reviews.class))).thenAnswer(invocation -> {
            Reviews review = invocation.getArgument(0);
            review.setId(1L);
            return review;
        });

        ReviewResponseDto response = reviewService.addReview(1L,dto,"testcustomer");

        assertNotNull(response);
        assertEquals(1L,response.reviewId());
        assertEquals(1L,response.bookingId());
        assertEquals("Test Customer",response.customerName());
        assertEquals(5,response.rating());
        assertEquals("Excellent stay",response.comment());
        assertNotNull(response.createdAt());

        verify(bookingRepository,times(1)).findById(1L);
        verify(reviewRepository,times(1)).save(any(Reviews.class));

        verifyNoMoreInteractions(bookingRepository);
        verifyNoMoreInteractions(reviewRepository);
    }

    @Test
    public void addReviewTest_NotFound(){
        ReviewRequestDto dto = new ReviewRequestDto(5,"Excellent stay");

        when(bookingRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                ()-> reviewService.addReview(5L,dto,"testcustomer"));

        verify(bookingRepository,times(1)).findById(5L);
        verifyNoMoreInteractions(bookingRepository);
        verifyNoInteractions(reviewRepository);
    }

    @Test
    public void addReviewTest_OwnerShipException(){
        User user = new User();
        user.setUsername("anothercustomer");

        Customer customer = new Customer();
        customer.setUser(user);

        Bookings booking = new Bookings();
        booking.setId(1L);
        booking.setCustomer(customer);
        booking.setBookingStatus(BookingStatus.COMPLETED);

        ReviewRequestDto dto = new ReviewRequestDto(5,"Excellent stay");

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(OwnerShipException.class,
                ()-> reviewService.addReview(1L,dto,"testcustomer"));

        verify(bookingRepository,times(1)).findById(1L);
        verifyNoMoreInteractions(bookingRepository);
        verifyNoInteractions(reviewRepository);
    }

    @Test
    public void addReviewTest_BookingNotCompleted(){
        User user = new User();
        user.setUsername("testcustomer");

        Customer customer = new Customer();
        customer.setUser(user);

        Bookings booking = new Bookings();
        booking.setId(1L);
        booking.setCustomer(customer);
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        ReviewRequestDto dto = new ReviewRequestDto(5,"Excellent stay");

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(InvalidCallException.class,
                ()-> reviewService.addReview(1L,dto,"testcustomer"));

        verify(bookingRepository,times(1)).findById(1L);
        verifyNoMoreInteractions(bookingRepository);
        verifyNoInteractions(reviewRepository);
    }

    @Test
    public void getHotelReviewSummaryTest(){
        Hotels hotel = new Hotels();
        hotel.setId(1L);
        hotel.setHotelStatus(HotelStatus.ACTIVE);
        hotel.setVerificationStatus(VerificationStatus.APPROVED);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(reviewRepository.getAverageRatingByHotelId(1L)).thenReturn(4.56);
        when(reviewRepository.countReviewsByHotelId(1L)).thenReturn(10L);

        HotelRatingSummaryDto response = reviewService.getHotelReviewSummary(1L);

        assertNotNull(response);
        assertEquals(1L,response.hotelId());
        assertEquals(4.6,response.averageRating());
        assertEquals(10L,response.totalReviews());

        verify(hotelRepository,times(1)).findById(1L);
        verify(reviewRepository,times(1)).getAverageRatingByHotelId(1L);
        verify(reviewRepository,times(1)).countReviewsByHotelId(1L);

        verifyNoMoreInteractions(hotelRepository);
        verifyNoMoreInteractions(reviewRepository);
    }

    @Test
    public void getHotelReviewSummaryTest_NotFound(){
        when(hotelRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                ()-> reviewService.getHotelReviewSummary(5L));

        verify(hotelRepository,times(1)).findById(5L);
        verifyNoMoreInteractions(hotelRepository);
        verifyNoInteractions(reviewRepository);
    }

    @Test
    public void getHotelReviewSummaryTest_NotApproved(){
        Hotels hotel = new Hotels();
        hotel.setId(1L);
        hotel.setHotelStatus(HotelStatus.ACTIVE);
        hotel.setVerificationStatus(VerificationStatus.PENDING);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        assertThrows(ResourceNotFoundException.class,
                ()-> reviewService.getHotelReviewSummary(1L));

        verify(hotelRepository,times(1)).findById(1L);
        verifyNoMoreInteractions(hotelRepository);
        verifyNoInteractions(reviewRepository);
    }

    @Test
    public void getMyReviewTest(){
        User user = new User();
        user.setUsername("testcustomer");

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Test Customer");
        customer.setUser(user);

        Bookings booking = new Bookings();
        booking.setId(1L);
        booking.setCustomer(customer);

        Reviews review = new Reviews();
        review.setId(1L);
        review.setBooking(booking);
        review.setRating(5);
        review.setComment("Excellent stay");

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(reviewRepository.findByBookingId(1L)).thenReturn(Optional.of(review));

        ReviewResponseDto response = reviewService.getMyReview(1L,"testcustomer");

        assertNotNull(response);
        assertEquals(1L,response.reviewId());
        assertEquals(1L,response.bookingId());
        assertEquals("Test Customer",response.customerName());
        assertEquals(5,response.rating());
        assertEquals("Excellent stay",response.comment());

        verify(bookingRepository,times(1)).findById(1L);
        verify(reviewRepository,times(1)).findByBookingId(1L);

        verifyNoMoreInteractions(bookingRepository);
        verifyNoMoreInteractions(reviewRepository);
    }

    @Test
    public void getMyReviewTest_NotFound(){
        User user = new User();
        user.setUsername("testcustomer");

        Customer customer = new Customer();
        customer.setUser(user);

        Bookings booking = new Bookings();
        booking.setId(1L);
        booking.setCustomer(customer);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(reviewRepository.findByBookingId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                ()-> reviewService.getMyReview(1L,"testcustomer"));

        verify(bookingRepository,times(1)).findById(1L);
        verify(reviewRepository,times(1)).findByBookingId(1L);

        verifyNoMoreInteractions(bookingRepository);
        verifyNoMoreInteractions(reviewRepository);
    }

    @Test
    public void getMyReviewTest_OwnerShipException(){
        User user = new User();
        user.setUsername("anothercustomer");

        Customer customer = new Customer();
        customer.setUser(user);

        Bookings booking = new Bookings();
        booking.setId(1L);
        booking.setCustomer(customer);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(OwnerShipException.class,
                ()-> reviewService.getMyReview(1L,"testcustomer"));

        verify(bookingRepository,times(1)).findById(1L);
        verifyNoMoreInteractions(bookingRepository);
        verifyNoInteractions(reviewRepository);
    }
}
