
package com.springboot.cozyheavenstay.service;

import com.springboot.cozyheavenstay.dto.request.OwnerRequestDto;
import com.springboot.cozyheavenstay.enums.BookingStatus;
import com.springboot.cozyheavenstay.enums.HotelStatus;
import com.springboot.cozyheavenstay.enums.PaymentStatus;
import com.springboot.cozyheavenstay.enums.Role;
import com.springboot.cozyheavenstay.exception.InvalidCallException;
import com.springboot.cozyheavenstay.exception.OwnerShipException;
import com.springboot.cozyheavenstay.exception.ResourceNotFoundException;
import com.springboot.cozyheavenstay.model.Hotels;
import com.springboot.cozyheavenstay.model.Owner;
import com.springboot.cozyheavenstay.model.User;
import com.springboot.cozyheavenstay.repository.OwnerRepository;
import com.springboot.cozyheavenstay.repository.UserRepository;
import com.springboot.cozyheavenstay.repository.HotelRepository;
import com.springboot.cozyheavenstay.repository.BookingRepository;
import com.springboot.cozyheavenstay.repository.BookingRoomsRepository;
import com.springboot.cozyheavenstay.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class OwnerServiceTest {
    @InjectMocks
    private OwnerService ownerService;
    @Mock
    private OwnerRepository ownerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingRoomsRepository bookingRoomsRepository;
    @Mock
    private PaymentRepository paymentRepository;

    @Test
    public void addOwnerTest(){
        OwnerRequestDto dto = new OwnerRequestDto(
                "testOwner",
                "owner@gmail.com",
                "9876543210",
                "Male",
                "Chennai",
                "Test Business",
                "testowner",
                "password"
        );

        User user = new User();

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        ownerService.addOwner(dto);

        verify(passwordEncoder,times(1)).encode("password");
        verify(userRepository,times(1)).save(any(User.class));
        verify(ownerRepository,times(1)).save(any(Owner.class));

        verifyNoMoreInteractions(passwordEncoder);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(ownerRepository);
    }

    @Test
    public void updateHotelStatusTest(){
        User user = new User();
        user.setUsername("testowner");

        Owner owner = new Owner();
        owner.setUser(user);

        Hotels hotel = new Hotels();
        hotel.setId(1L);
        hotel.setOwner(owner);
        hotel.setHotelStatus(HotelStatus.INACTIVE);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(hotelRepository.save(hotel)).thenReturn(hotel);

        ownerService.updateHotelStatus(1L,HotelStatus.ACTIVE,"testowner");

        assertEquals(HotelStatus.ACTIVE,hotel.getHotelStatus());

        verify(hotelRepository,times(1)).findById(1L);
        verify(hotelRepository,times(1)).save(hotel);

        verifyNoMoreInteractions(hotelRepository);
    }

    @Test
    public void updateHotelStatusTest_NotFound(){
        when(hotelRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                ()-> ownerService.updateHotelStatus(5L,HotelStatus.ACTIVE,"testowner"));

        verify(hotelRepository,times(1)).findById(5L);
        verify(hotelRepository,times(0)).save(any(Hotels.class));

        verifyNoMoreInteractions(hotelRepository);
    }

    @Test
    public void updateHotelStatusTest_OwnerShipException(){
        User user = new User();
        user.setUsername("anotherowner");

        Owner owner = new Owner();
        owner.setUser(user);

        Hotels hotel = new Hotels();
        hotel.setId(1L);
        hotel.setOwner(owner);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        assertThrows(OwnerShipException.class,
                ()-> ownerService.updateHotelStatus(1L,HotelStatus.ACTIVE,"testowner"));

        verify(hotelRepository,times(1)).findById(1L);
        verify(hotelRepository,times(0)).save(any(Hotels.class));

        verifyNoMoreInteractions(hotelRepository);
    }

    @Test
    public void getRefundRequestedBookingsTest(){
        when(bookingRepository.getRefundRequestedBookings(
                BookingStatus.CANCELLED,
                PaymentStatus.PAID,
                "testowner"
        )).thenReturn(Collections.emptyList());

        var response = ownerService.getRefundRequestedBookings("testowner");

        assertNotNull(response);
        assertEquals(0,response.size());

        verify(bookingRepository,times(1)).getRefundRequestedBookings(
                BookingStatus.CANCELLED,
                PaymentStatus.PAID,
                "testowner"
        );

        verifyNoMoreInteractions(bookingRepository);
    }
}
