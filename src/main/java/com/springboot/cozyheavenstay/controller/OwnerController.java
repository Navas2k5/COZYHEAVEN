package com.springboot.cozyheavenstay.controller;

import com.springboot.cozyheavenstay.dto.request.OwnerRequestDto;
import com.springboot.cozyheavenstay.dto.response.BookingResponseDto;
import com.springboot.cozyheavenstay.dto.response.HotelResponseDto;
import com.springboot.cozyheavenstay.dto.response.OwnerBookingFilterDto;
import com.springboot.cozyheavenstay.dto.response.OwnerBookingResponseDto;
import com.springboot.cozyheavenstay.enums.HotelStatus;
import com.springboot.cozyheavenstay.service.HotelService;
import com.springboot.cozyheavenstay.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class OwnerController {

    private final OwnerService ownerService;
    private final HotelService hotelService;

    @PostMapping("/api/owner/add")
    public void addOwner(@Valid @RequestBody OwnerRequestDto ownerRequestDto){
        ownerService.addOwner(ownerRequestDto);
    }

    @GetMapping("/api/owner/hotel/all")
    public List<HotelResponseDto> getHotelByOwnerUsername(Principal principal){
        return hotelService.getHotelByOwnerUsername(principal.getName());
    }

    @PutMapping("/api/owner/hotel/status/{hotelId}")
    public void updateHotelStatus(@PathVariable Long hotelId,
                                  @RequestParam HotelStatus hotelStatus,
                                  Principal principal){
        ownerService.updateHotelStatus(hotelId,hotelStatus,principal.getName());
    }

    @GetMapping("/api/owner/hotel/{hotelId}/bookings")
    public OwnerBookingFilterDto getHotelBookings(@PathVariable Long hotelId,
                                                  Principal principal,
                                                  @RequestParam(name = "page",required = false,defaultValue = "0") Integer page,
                                                  @RequestParam(name = "size",required = false,defaultValue = "20") Integer size){
        return ownerService.getHotelBookings(hotelId, principal.getName(),page,size);
    }


    @GetMapping("/api/owner/hotel/{hotelId}")
    public HotelResponseDto getOwnerHotelById(
            @PathVariable Long hotelId,
            Principal principal) {

        return hotelService.getHotelByIdAndOwnerUsername(
                hotelId,
                principal.getName()
        );
    }

    @GetMapping("/api/owner/refund-requests")
    public List<OwnerBookingResponseDto> getRefundRequestedBookings(
            Principal principal) {

        return ownerService.getRefundRequestedBookings(principal.getName());
    }


}
