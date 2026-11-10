package com.springboot.cozyheavenstay.service;

import com.springboot.cozyheavenstay.dto.request.RoomRequestDto;
import com.springboot.cozyheavenstay.dto.response.*;
import com.springboot.cozyheavenstay.enums.*;
import com.springboot.cozyheavenstay.exception.InvalidCallException;
import com.springboot.cozyheavenstay.exception.OwnerShipException;
import com.springboot.cozyheavenstay.exception.ResourceNotFoundException;
import com.springboot.cozyheavenstay.mapper.RoomMapper;
import com.springboot.cozyheavenstay.model.Hotels;
import com.springboot.cozyheavenstay.model.Rooms;
import com.springboot.cozyheavenstay.repository.HotelRepository;
import com.springboot.cozyheavenstay.repository.RoomRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    private record RoomOptionKey(
            Double roomSize,
            BedType bedType,
            Integer maxOccupancy,
            Double baseFare,
            Boolean ac
    ) {}

    @Transactional
    public void add(Long hotelId, @Valid RoomRequestDto roomRequestDto, String OwnerUsername) {
        log.info("Adding room for hotel ID: {}", hotelId);
        //find hotel
        Hotels hotel = hotelRepository.findById(hotelId).orElseThrow(()->
                new ResourceNotFoundException("Hotel Not Found"));

        //check hotel ownership
        if(!hotel.getOwner().getUser().getUsername().equals(OwnerUsername))
            throw new OwnerShipException("You can add rooms only to your hotels");

        validateRoomCapacity(roomRequestDto);

        //check duplicate room number
        String roomNumber = roomRequestDto.roomNumber().trim();
        if(roomRepository.countByHotelAndRoomNumber(hotelId,roomNumber)>0)
            throw new InvalidCallException("Room number already exists in hotel");

        //map dto to entity
        Rooms room = RoomMapper.convertDtoToEntity(roomRequestDto);

        //attach hotel and status
        room.setHotel(hotel);
        room.setRoomStatus(RoomStatus.ACTIVE);

        //save room
        roomRepository.save(room);
        log.info("Room created successfully. Room ID: {}, Hotel ID: {}",
                room.getId(), hotelId);
    }

    private void validateRoomCapacity(RoomRequestDto roomRequestDto){
        int maximumAllowed = switch (roomRequestDto.bedType()){
            case SINGLE -> 2;
            case DOUBLE -> 4;
            case KING -> 6;
        };

        if(roomRequestDto.maxOccupancy()>maximumAllowed){
            throw new InvalidCallException("Maximum Occupancy for "+roomRequestDto.bedType()
                +" cannot exceed "+ maximumAllowed);
        }
    }

    public RoomFilterDto getRoomsByHotel(Long hotelId, String OwnerUsername,Integer page,Integer size) {

        // validate pagination
        if (page < 0 || size < 1 || size > 100)
            throw new InvalidCallException(
                    "Page must be 0 or above and size must be between 1 and 100");

        // prepare pagination
        Pageable pageable = PageRequest.of(page, size);
        //find hotel
        Hotels hotel = hotelRepository.findById(hotelId).orElseThrow(()->
                new ResourceNotFoundException("Hotel Not Found"));

        //check hotel ownership
        if(!hotel.getOwner().getUser().getUsername().equals(OwnerUsername))
            throw new OwnerShipException("You can view rooms only from your hotels");

        // fetch rooms
        Page<Rooms> pageDto = roomRepository.getRoomsByHotelId(hotelId, pageable);

        // convert entity to response dto
        List<RoomResponseDto> rooms = pageDto.getContent()
                .stream()
                .map(RoomMapper::convertEntityToDto)
                .toList();

        return new RoomFilterDto(
                rooms,
                new PaginationDto(
                        pageDto.getTotalPages(),
                        pageDto.getTotalElements(),
                        pageDto.getContent().size()
                )
        );
    }

    @Transactional
    public void updateRoomStatus(Long roomId, RoomStatus roomStatus, String ownerUsername) {
        log.info("Updating status of room ID: {} to {}",
                roomId, roomStatus);
        //find room
        Rooms room = roomRepository.findById(roomId).orElseThrow(()->
                new ResourceNotFoundException("Room Does not Exist"));

        //check hotel ownership
        if(!room.getHotel().getOwner().getUser().getUsername().equals(ownerUsername))
            throw new OwnerShipException("You can modify only your hotel's room");

        //validate status
        if(roomStatus==null)
            throw new InvalidCallException("room status is reqquired");

        //update and save
        room.setRoomStatus(roomStatus);

        roomRepository.save(room);
        log.info("Room status updated successfully. Room ID: {}", roomId);
    }

    @Transactional
    public void updateRoom(Long roomId, RoomRequestDto roomRequestDto, String ownerUsername) {
        log.info("Updating room details. Room ID: {}", roomId);
        // find room
        Rooms room = roomRepository.findById(roomId).orElseThrow(()->
                new ResourceNotFoundException("Room not found"));

        // check ownership
        if(!room.getHotel().getOwner().getUser().getUsername().equals(ownerUsername))
            throw new OwnerShipException("You can modify only your hotel's room");

        //validate capacity
        validateRoomCapacity(roomRequestDto);

        //check duplicate room number
        String roomNumber= roomRequestDto.roomNumber().trim();
        if(!room.getRoomNumber().equals(roomNumber)){
            if(roomRepository.countByHotelAndRoomNumber(room.getHotel().getId(),roomNumber)>0){
                throw new InvalidCallException("Room number already exists in hotel");
            }
        }

        //update existing room
        RoomMapper.updateEntityFromDto(roomRequestDto,room);
        roomRepository.save(room);
        log.info("Room updated successfully. Room ID: {}", roomId);
    }

    public List<RoomResponseDto> getAvailableRooms(Long hotelId,
                                                   LocalDate checkIn,
                                                   LocalDate checkOut,
                                                   BedType bedType,
                                                   Boolean ac,
                                                   Double minFare,
                                                   Double maxFare,
                                                   Integer guests,
                                                   Integer page,
                                                   Integer size){
        //validate dates
        LocalDate today = LocalDate.now();
        if(checkIn.isBefore(today))
            throw new InvalidCallException("Check-in cannot be in the past");

        if(!checkOut.isAfter(checkIn))
            throw new InvalidCallException("Check-out must be after check-in");

        //validate optional filters
        if(guests!=null && guests<1)
            throw new InvalidCallException("Guest count must be atleast 1");
        if(page<0||size<1||size>100)
            throw new InvalidCallException("Page must be 0 or above, and size between 1 and 100");

        Hotels hotel = hotelRepository.findById(hotelId).orElseThrow(()->
                new ResourceNotFoundException("Hotel not found"));

        if(hotel.getVerificationStatus()!=VerificationStatus.APPROVED || hotel.getHotelStatus()!= HotelStatus.ACTIVE)
            throw new ResourceNotFoundException("Hotel not found");

        //prepare pagination
        Pageable pageable= PageRequest.of(page,size);

        //fetch matching rooms and return
        return roomRepository.dynamicFilterRooms(hotelId,
                RoomStatus.ACTIVE,bedType,ac,minFare,maxFare,guests,checkIn,checkOut,
                List.of(BookingStatus.PENDING,BookingStatus.CONFIRMED),
                pageable);

    }

    public List<RoomOptionResponseDto> getAvailableRoomOptions(
            Long hotelId,
            LocalDate checkIn,
            LocalDate checkOut,
            BedType bedType,
            Boolean ac,
            Double minFare,
            Double maxFare,
            Integer guests) {

        // Validate dates
        LocalDate today = LocalDate.now();

        if (checkIn.isBefore(today))
            throw new InvalidCallException("Check-in cannot be in the past");

        if (!checkOut.isAfter(checkIn))
            throw new InvalidCallException("Check-out must be after check-in");

        // Validate optional filters
        if (guests != null && guests < 1)
            throw new InvalidCallException("Guest count must be atleast 1");

        // Find hotel
        Hotels hotel = hotelRepository.findById(hotelId).orElseThrow(() ->
                new ResourceNotFoundException("Hotel not found"));

        if (hotel.getVerificationStatus() != VerificationStatus.APPROVED ||
                hotel.getHotelStatus() != HotelStatus.ACTIVE)
            throw new ResourceNotFoundException("Hotel not found");

        // Fetch all matching rooms without pagination
        List<RoomResponseDto> rooms = roomRepository.dynamicFilterRooms(
                hotelId,
                RoomStatus.ACTIVE,
                bedType,
                ac,
                minFare,
                maxFare,
                guests,
                checkIn,
                checkOut,
                List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED),
                Pageable.unpaged()
        );

        // Group rooms based on their features
        Map<RoomOptionKey, List<RoomResponseDto>> groupedRooms = rooms.stream()
                .collect(Collectors.groupingBy(room ->
                        new RoomOptionKey(
                                room.roomSize(),
                                room.bedType(),
                                room.maxOccuppancy(),
                                room.baseFare(),
                                room.ac()
                        )
                ));

        // Convert each group into a response DTO
        return groupedRooms.entrySet().stream()
                .map(entry -> {
                    List<RoomResponseDto> availableRooms = entry.getValue();

                    RoomOptionKey key = entry.getKey();

                    return new RoomOptionResponseDto(
                            key.roomSize(),
                            key.bedType(),
                            key.maxOccupancy(),
                            key.baseFare(),
                            key.ac(),
                            (long) availableRooms.size(),
                            availableRooms.stream()
                                    .map(RoomResponseDto::id)
                                    .toList()
                    );
                })
                .toList();
    }

    public RoomSummaryDto getRoomSummary(Long hotelId, String ownerUsername) {

        // find hotel
        Hotels hotel = hotelRepository.findById(hotelId).orElseThrow(() ->
                new ResourceNotFoundException("Hotel Not Found"));

        // check hotel ownership
        if (!hotel.getOwner().getUser().getUsername().equals(ownerUsername))
            throw new OwnerShipException("You can view rooms only from your hotels");

        // fetch room counts
        Long totalRooms = roomRepository.countTotalRoomsByHotelId(hotelId);

        Long activeRooms = roomRepository.countRoomsByHotelIdAndStatus(
                hotelId, RoomStatus.ACTIVE);

        Long inactiveRooms = roomRepository.countRoomsByHotelIdAndStatus(
                hotelId, RoomStatus.INACTIVE);

        Long maintenanceRooms = roomRepository.countRoomsByHotelIdAndStatus(
                hotelId, RoomStatus.MAINTENANCE);

        return new RoomSummaryDto(
                totalRooms,
                activeRooms,
                inactiveRooms,
                maintenanceRooms
        );
    }
}
