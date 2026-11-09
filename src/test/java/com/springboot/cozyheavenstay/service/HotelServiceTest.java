
package com.springboot.cozyheavenstay.service;

import com.springboot.cozyheavenstay.dto.request.HotelRequestDto;
import com.springboot.cozyheavenstay.dto.response.CustomerHotelResponseDto;
import com.springboot.cozyheavenstay.enums.HotelStatus;
import com.springboot.cozyheavenstay.enums.VerificationStatus;
import com.springboot.cozyheavenstay.exception.OwnerShipException;
import com.springboot.cozyheavenstay.exception.ResourceNotFoundException;
import com.springboot.cozyheavenstay.model.Hotels;
import com.springboot.cozyheavenstay.model.Owner;
import com.springboot.cozyheavenstay.model.User;
import com.springboot.cozyheavenstay.repository.HotelRepository;
import com.springboot.cozyheavenstay.repository.OwnerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class HotelServiceTest {
    @InjectMocks
    private HotelService hotelService;
    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private OwnerRepository ownerRepository;

    @Test
    public void addHotelTest(){
        Owner owner = new Owner();
        owner.setId(1L);

        HotelRequestDto dto = new HotelRequestDto(
                "Test Hotel",
                "A good hotel",
                "Chennai",
                "Chennai Main Road",
                "WiFi,Pool"
        );

        when(ownerRepository.getByUsername("testowner")).thenReturn(owner);
        when(hotelRepository.save(any(Hotels.class))).thenAnswer(invocation -> {
            Hotels hotel = invocation.getArgument(0);
            hotel.setId(1L);
            return hotel;
        });

        Long hotelId = hotelService.add("testowner",dto);

        assertEquals(1L,hotelId);

        verify(ownerRepository,times(1)).getByUsername("testowner");
        verify(hotelRepository,times(1)).save(any(Hotels.class));

        verifyNoMoreInteractions(ownerRepository);
        verifyNoMoreInteractions(hotelRepository);
    }

    @Test
    public void addHotelTest_OwnerNotFound(){
        HotelRequestDto dto = new HotelRequestDto(
                "Test Hotel",
                "A good hotel",
                "Chennai",
                "Chennai Main Road",
                "WiFi,Pool"
        );

        when(ownerRepository.getByUsername("wrongowner")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                ()-> hotelService.add("wrongowner",dto));

        verify(ownerRepository,times(1)).getByUsername("wrongowner");
        verifyNoInteractions(hotelRepository);
    }

    @Test
    public void updateHotelTest(){
        User user = new User();
        user.setUsername("testowner");

        Owner owner = new Owner();
        owner.setUser(user);

        Hotels hotel = new Hotels();
        hotel.setId(1L);
        hotel.setOwner(owner);
        hotel.setVerificationStatus(VerificationStatus.APPROVED);

        HotelRequestDto dto = new HotelRequestDto(
                "Updated Hotel",
                "Updated description",
                "Bangalore",
                "Updated address",
                "WiFi,Gym"
        );

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(hotelRepository.save(hotel)).thenReturn(hotel);

        hotelService.updateHotel(1L,dto,"testowner");

        assertEquals("Updated Hotel",hotel.getHotelName());
        assertEquals("Updated description",hotel.getDescription());
        assertEquals(VerificationStatus.PENDING,hotel.getVerificationStatus());

        verify(hotelRepository,times(1)).findById(1L);
        verify(hotelRepository,times(1)).save(hotel);

        verifyNoMoreInteractions(hotelRepository);
    }

    @Test
    public void updateHotelTest_NotFound(){
        HotelRequestDto dto = new HotelRequestDto(
                "Updated Hotel",
                "Updated description",
                "Bangalore",
                "Updated address",
                "WiFi,Gym"
        );

        when(hotelRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                ()-> hotelService.updateHotel(5L,dto,"testowner"));

        verify(hotelRepository,times(1)).findById(5L);
        verify(hotelRepository,times(0)).save(any(Hotels.class));

        verifyNoMoreInteractions(hotelRepository);
    }

    @Test
    public void updateHotelTest_OwnerShipException(){
        User user = new User();
        user.setUsername("anotherowner");

        Owner owner = new Owner();
        owner.setUser(user);

        Hotels hotel = new Hotels();
        hotel.setId(1L);
        hotel.setOwner(owner);

        HotelRequestDto dto = new HotelRequestDto(
                "Updated Hotel",
                "Updated description",
                "Bangalore",
                "Updated address",
                "WiFi,Gym"
        );

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        assertThrows(OwnerShipException.class,
                ()-> hotelService.updateHotel(1L,dto,"testowner"));

        verify(hotelRepository,times(1)).findById(1L);
        verify(hotelRepository,times(0)).save(any(Hotels.class));

        verifyNoMoreInteractions(hotelRepository);
    }

    @Test
    public void getHotelByIdTest(){
        Hotels hotel = new Hotels();
        hotel.setId(1L);
        hotel.setHotelName("Test Hotel");
        hotel.setDescription("A good hotel");
        hotel.setLocation("Chennai");
        hotel.setAddress("Chennai Main Road");
        hotel.setAmenities("WiFi,Pool");
        hotel.setHotelStatus(HotelStatus.ACTIVE);
        hotel.setVerificationStatus(VerificationStatus.APPROVED);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        CustomerHotelResponseDto response = hotelService.getHotelById(1L);

        assertNotNull(response);
        assertEquals(1L,response.id());
        assertEquals("Test Hotel",response.hotelName());

        verify(hotelRepository,times(1)).findById(1L);

        verifyNoMoreInteractions(hotelRepository);
    }

    @Test
    public void getHotelByIdTest_NotFound(){
        when(hotelRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                ()-> hotelService.getHotelById(5L));

        verify(hotelRepository,times(1)).findById(5L);

        verifyNoMoreInteractions(hotelRepository);
    }

    @Test
    public void getHotelByIdTest_NotApproved(){
        Hotels hotel = new Hotels();
        hotel.setId(1L);
        hotel.setHotelStatus(HotelStatus.ACTIVE);
        hotel.setVerificationStatus(VerificationStatus.PENDING);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        assertThrows(ResourceNotFoundException.class,
                ()-> hotelService.getHotelById(1L));

        verify(hotelRepository,times(1)).findById(1L);

        verifyNoMoreInteractions(hotelRepository);
    }
}
