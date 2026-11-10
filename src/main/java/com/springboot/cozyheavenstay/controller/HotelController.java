package com.springboot.cozyheavenstay.controller;

import com.springboot.cozyheavenstay.dto.request.HotelRequestDto;
import com.springboot.cozyheavenstay.dto.response.CustomerHotelResponseDto;
import com.springboot.cozyheavenstay.dto.response.FilterDto;
import com.springboot.cozyheavenstay.dto.response.HotelResponseDto;
import com.springboot.cozyheavenstay.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173/")
public class HotelController {


    private final HotelService hotelService;

    @PostMapping("/api/hotel/add")
    public Long addHotel(@Valid @RequestBody HotelRequestDto hotelRequestDto,
                         Principal principal){
        log.info("Request received to add hotel by owner: {}", principal.getName());
        return hotelService.add(principal.getName(),hotelRequestDto);
    }

    @PutMapping("/api/hotel/{hotelId}/update")
    public void updateHotel(@PathVariable Long hotelId,
                            @Valid @RequestBody HotelRequestDto hotelRequestDto,
                            Principal principal){
        log.info("Request received to update hotel ID: {} by owner: {}",
                hotelId, principal.getName());
        hotelService.updateHotel(hotelId,hotelRequestDto,principal.getName());
        log.info("Hotel updated successfully. Hotel ID: {}", hotelId);
    }

    @GetMapping("/api/hotel/all")
    public FilterDto getAllHotels(@RequestParam(name = "location",required = false) String location,
                                  @RequestParam(name = "hotelName", required = false) String hotelName,
                                  @RequestParam(name = "checkIn", required = false) LocalDate checkIn,
                                  @RequestParam(name = "checkOut", required = false) LocalDate checkOut,
                                  @RequestParam(name = "page",required = false,defaultValue = "0") Integer page,
                                  @RequestParam(name = "size",required = false,defaultValue = "20") Integer size){
        return hotelService.getAllHotels(location,hotelName,checkIn,checkOut,page,size);
    }

    @GetMapping("/api/hotel/{hotelId}")
    public CustomerHotelResponseDto getHotelById(@PathVariable Long hotelId){
        return hotelService.getHotelById(hotelId);
    }

    @PostMapping("/api/owner/hotel/{hotelId}/upload")
    public void upload(
            Principal principal,
            @PathVariable Long hotelId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        hotelService.upload(
                hotelId,
                principal.getName(),
                file
        );

    }


}
