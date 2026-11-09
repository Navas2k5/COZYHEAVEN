package com.springboot.cozyheavenstay.controller;

import com.springboot.cozyheavenstay.dto.request.BookingQuoteRequestDto;
import com.springboot.cozyheavenstay.dto.response.BookingDetailsResponseDto;
import com.springboot.cozyheavenstay.dto.response.BookingQuoteResponseDto;
import com.springboot.cozyheavenstay.dto.response.BookingResponseDto;
import com.springboot.cozyheavenstay.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/api/booking/quote")
    public BookingQuoteResponseDto getQuote(@Valid @RequestBody BookingQuoteRequestDto dto){
        return bookingService.getQuote(dto);
    }

    @PostMapping("/api/booking/add")
    public BookingResponseDto addBooking(@Valid @RequestBody BookingQuoteRequestDto dto,
                                         Principal principal){
        return bookingService.addBooking(dto,principal.getName());
    }

    @GetMapping("/api/booking/my")
    public List<BookingResponseDto> getMyBookings(Principal principal,
                                                  @RequestParam(value = "page",required = false,defaultValue = "0") Integer page,
                                                  @RequestParam(value = "size",required = false,defaultValue = "20") Integer size){
        return bookingService.getMyBookings(principal.getName(),page,size);
    }

    @GetMapping("/api/booking/{bookingId}")
    public BookingDetailsResponseDto getBookingDetailsById(@PathVariable Long bookingId,
                                                           Principal principal){
        return bookingService.getBookingDetails(bookingId, principal.getName());
    }

    @PutMapping("/api/booking/{bookingId}/cancel")
    public BookingResponseDto cancelBooking(@PathVariable Long bookingId,
                                            Principal principal){
        return bookingService.cancelBooking(bookingId, principal.getName());
    }
}
