
package com.springboot.cozyheavenstay.service;

import com.springboot.cozyheavenstay.dto.request.RoomRequestDto;
import com.springboot.cozyheavenstay.dto.response.RoomResponseDto;
import com.springboot.cozyheavenstay.enums.BedType;
import com.springboot.cozyheavenstay.enums.HotelStatus;
import com.springboot.cozyheavenstay.enums.RoomStatus;
import com.springboot.cozyheavenstay.enums.VerificationStatus;
import com.springboot.cozyheavenstay.exception.InvalidCallException;
import com.springboot.cozyheavenstay.exception.OwnerShipException;
import com.springboot.cozyheavenstay.exception.ResourceNotFoundException;
import com.springboot.cozyheavenstay.model.Hotels;
import com.springboot.cozyheavenstay.model.Owner;
import com.springboot.cozyheavenstay.model.Rooms;
import com.springboot.cozyheavenstay.model.User;
import com.springboot.cozyheavenstay.repository.HotelRepository;
import com.springboot.cozyheavenstay.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {
    @InjectMocks
    private RoomService roomService;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private HotelRepository hotelRepository;

    @Test
    public void addRoomTest(){
        User user = new User();
        user.setUsername("testowner");

        Owner owner = new Owner();
        owner.setUser(user);

        Hotels hotel = new Hotels();
        hotel.setId(1L);
        hotel.setOwner(owner);

        RoomRequestDto dto = new RoomRequestDto(
                "101",
                250.0,
                BedType.DOUBLE,
                4,
                2000.0,
                true
        );

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(roomRepository.countByHotelAndRoomNumber(1L,"101")).thenReturn(0L);

        roomService.add(1L,dto,"testowner");

        verify(hotelRepository,times(1)).findById(1L);
        verify(roomRepository,times(1)).countByHotelAndRoomNumber(1L,"101");
        verify(roomRepository,times(1)).save(any(Rooms.class));

        verifyNoMoreInteractions(hotelRepository);
        verifyNoMoreInteractions(roomRepository);
    }

    @Test
    public void addRoomTest_DuplicateRoomNumber(){
        User user = new User();
        user.setUsername("testowner");

        Owner owner = new Owner();
        owner.setUser(user);

        Hotels hotel = new Hotels();
        hotel.setId(1L);
        hotel.setOwner(owner);

        RoomRequestDto dto = new RoomRequestDto(
                "101",
                250.0,
                BedType.DOUBLE,
                4,
                2000.0,
                true
        );

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(roomRepository.countByHotelAndRoomNumber(1L,"101")).thenReturn(1L);

        assertThrows(InvalidCallException.class,
                ()-> roomService.add(1L,dto,"testowner"));

        verify(hotelRepository,times(1)).findById(1L);
        verify(roomRepository,times(1)).countByHotelAndRoomNumber(1L,"101");
        verify(roomRepository,times(0)).save(any(Rooms.class));

        verifyNoMoreInteractions(hotelRepository);
        verifyNoMoreInteractions(roomRepository);
    }

    @Test
    public void updateRoomTest(){
        User user = new User();
        user.setUsername("testowner");

        Owner owner = new Owner();
        owner.setUser(user);

        Hotels hotel = new Hotels();
        hotel.setId(1L);
        hotel.setOwner(owner);

        Rooms room = new Rooms();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setHotel(hotel);

        RoomRequestDto dto = new RoomRequestDto(
                "102",
                300.0,
                BedType.DOUBLE,
                4,
                2500.0,
                true
        );

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.countByHotelAndRoomNumber(1L,"102")).thenReturn(0L);

        roomService.updateRoom(1L,dto,"testowner");

        assertEquals("102",room.getRoomNumber());
        assertEquals(300.0,room.getRoomSize());
        assertEquals(2500.0,room.getBaseFare());

        verify(roomRepository,times(1)).findById(1L);
        verify(roomRepository,times(1)).countByHotelAndRoomNumber(1L,"102");
        verify(roomRepository,times(1)).save(room);

        verifyNoMoreInteractions(roomRepository);
    }

    @Test
    public void updateRoomTest_NotFound(){
        RoomRequestDto dto = new RoomRequestDto(
                "102",
                300.0,
                BedType.DOUBLE,
                4,
                2500.0,
                true
        );

        when(roomRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                ()-> roomService.updateRoom(5L,dto,"testowner"));

        verify(roomRepository,times(1)).findById(5L);
        verifyNoMoreInteractions(roomRepository);
    }

    @Test
    public void updateRoomStatusTest(){
        User user = new User();
        user.setUsername("testowner");

        Owner owner = new Owner();
        owner.setUser(user);

        Hotels hotel = new Hotels();
        hotel.setOwner(owner);

        Rooms room = new Rooms();
        room.setId(1L);
        room.setHotel(hotel);
        room.setRoomStatus(RoomStatus.ACTIVE);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        roomService.updateRoomStatus(1L,RoomStatus.INACTIVE,"testowner");

        assertEquals(RoomStatus.INACTIVE,room.getRoomStatus());

        verify(roomRepository,times(1)).findById(1L);
        verify(roomRepository,times(1)).save(room);

        verifyNoMoreInteractions(roomRepository);
    }

    @Test
    public void updateRoomStatusTest_OwnerShipException(){
        User user = new User();
        user.setUsername("anotherowner");

        Owner owner = new Owner();
        owner.setUser(user);

        Hotels hotel = new Hotels();
        hotel.setOwner(owner);

        Rooms room = new Rooms();
        room.setId(1L);
        room.setHotel(hotel);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        assertThrows(OwnerShipException.class,
                ()-> roomService.updateRoomStatus(1L,RoomStatus.INACTIVE,"testowner"));

        verify(roomRepository,times(1)).findById(1L);
        verify(roomRepository,times(0)).save(any(Rooms.class));

        verifyNoMoreInteractions(roomRepository);
    }

    @Test
    public void getAvailableRoomsTest(){
        Hotels hotel = new Hotels();
        hotel.setId(1L);
        hotel.setVerificationStatus(VerificationStatus.APPROVED);
        hotel.setHotelStatus(HotelStatus.ACTIVE);

        LocalDate checkIn = LocalDate.now().plusDays(2);
        LocalDate checkOut = LocalDate.now().plusDays(4);

        RoomResponseDto room = new RoomResponseDto(
                1L,
                "101",
                250.0,
                BedType.DOUBLE,
                4,
                2000.0,
                true,
                RoomStatus.ACTIVE
        );

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(roomRepository.dynamicFilterRooms(
                eq(1L),
                eq(RoomStatus.ACTIVE),
                eq(BedType.DOUBLE),
                eq(true),
                isNull(),
                isNull(),
                eq(2),
                eq(checkIn),
                eq(checkOut),
                anyList(),
                any(Pageable.class)
        )).thenReturn(List.of(room));

        List<RoomResponseDto> response = roomService.getAvailableRooms(
                1L,checkIn,checkOut,BedType.DOUBLE,true,null,null,2,0,10
        );

        assertNotNull(response);
        assertEquals(1,response.size());
        assertEquals("101",response.get(0).roomNumber());

        verify(hotelRepository,times(1)).findById(1L);
        verify(roomRepository,times(1)).dynamicFilterRooms(
                eq(1L),
                eq(RoomStatus.ACTIVE),
                eq(BedType.DOUBLE),
                eq(true),
                isNull(),
                isNull(),
                eq(2),
                eq(checkIn),
                eq(checkOut),
                anyList(),
                any(Pageable.class)
        );

        verifyNoMoreInteractions(hotelRepository);
        verifyNoMoreInteractions(roomRepository);
    }

    @Test
    public void getAvailableRoomsTest_InvalidDate(){
        LocalDate checkIn = LocalDate.now().minusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(2);

        assertThrows(InvalidCallException.class,
                ()-> roomService.getAvailableRooms(
                        1L,checkIn,checkOut,null,null,null,null,null,0,10
                ));

        verifyNoInteractions(hotelRepository);
        verifyNoInteractions(roomRepository);
    }

    @Test
    public void getAvailableRoomsTest_HotelNotFound(){
        LocalDate checkIn = LocalDate.now().plusDays(2);
        LocalDate checkOut = LocalDate.now().plusDays(4);

        when(hotelRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                ()-> roomService.getAvailableRooms(
                        1L,checkIn,checkOut,null,null,null,null,null,0,10
                ));

        verify(hotelRepository,times(1)).findById(1L);
        verifyNoInteractions(roomRepository);
    }
}
