package com.springboot.cozyheavenstay.controller;

import com.springboot.cozyheavenstay.dto.request.RoomRequestDto;
import com.springboot.cozyheavenstay.dto.response.RoomFilterDto;
import com.springboot.cozyheavenstay.dto.response.RoomOptionResponseDto;
import com.springboot.cozyheavenstay.dto.response.RoomResponseDto;
import com.springboot.cozyheavenstay.dto.response.RoomSummaryDto;
import com.springboot.cozyheavenstay.enums.BedType;
import com.springboot.cozyheavenstay.enums.RoomStatus;
import com.springboot.cozyheavenstay.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173/")
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/api/room/hotel/{hotelId}/add")
    public void addRoom(@PathVariable Long hotelId,
                        @Valid @RequestBody RoomRequestDto roomRequestDto,
                        Principal principal){
        roomService.add(hotelId,roomRequestDto,principal.getName());
    }

    @GetMapping("/api/room/hotel/{hotelId}/all")
    public RoomFilterDto getRooms(@PathVariable Long hotelId,
                                  Principal principal,
                                  @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                  @RequestParam(name = "size", required = false, defaultValue = "10") Integer size){
        return roomService.getRoomsByHotel(hotelId, principal.getName(),page,size);
    }

    @PutMapping("/api/room/{roomId}/status")
    public void updateRoomStatus(@PathVariable Long roomId,
                                 @RequestParam RoomStatus roomStatus,
                                 Principal principal){
        roomService.updateRoomStatus(roomId,roomStatus,principal.getName());
    }

    @PutMapping("/api/room/{roomId}/update")
    public void updateRoom(@PathVariable Long roomId,
                                 @RequestBody RoomRequestDto roomRequestDto,
                                 Principal principal){
        roomService.updateRoom(roomId,roomRequestDto,principal.getName());
    }

    @GetMapping("/api/room/hotel/{hotelId}/available")
    public List<RoomResponseDto> getAvailableRooms(@PathVariable Long hotelId,
                                                   @RequestParam(name = "checkIn") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
                                                   @RequestParam(name = "checkOut")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
                                                   @RequestParam(name = "bedType", required = false) BedType bedType,
                                                   @RequestParam(name = "ac",required = false) Boolean ac,
                                                   @RequestParam(name = "minFare",required = false) Double minFare,
                                                   @RequestParam(name = "maxFare",required = false) Double maxFare,
                                                   @RequestParam(name = "guests",required = false) Integer guests,
                                                   @RequestParam(name = "page",required = false,defaultValue = "0") Integer page,
                                                   @RequestParam(name = "size",required = false,defaultValue = "10") Integer size
                                                   ){
        return roomService.getAvailableRooms(hotelId,checkIn,checkOut,bedType,ac,minFare,maxFare,guests,page,size);
    }

    @GetMapping("/api/room/hotel/{hotelId}/available-options")
    public List<RoomOptionResponseDto> getAvailableRoomOptions(
            @PathVariable Long hotelId,
            @RequestParam(name = "checkIn")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(name = "checkOut")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(name = "bedType", required = false) BedType bedType,
            @RequestParam(name = "ac", required = false) Boolean ac,
            @RequestParam(name = "minFare", required = false) Double minFare,
            @RequestParam(name = "maxFare", required = false) Double maxFare,
            @RequestParam(name = "guests", required = false) Integer guests) {

        return roomService.getAvailableRoomOptions(
                hotelId,
                checkIn,
                checkOut,
                bedType,
                ac,
                minFare,
                maxFare,
                guests
        );
    }

    @GetMapping("/api/room/hotel/{hotelId}/summary")
    public RoomSummaryDto getRoomSummary(
            @PathVariable Long hotelId,
            Principal principal) {

        return roomService.getRoomSummary(
                hotelId,
                principal.getName()
        );
    }

}
