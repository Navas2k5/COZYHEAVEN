
package com.springboot.cozyheavenstay.service;

import com.springboot.cozyheavenstay.dto.request.AdminRequestDto;
import com.springboot.cozyheavenstay.dto.response.AdminUserResponseDto;
import com.springboot.cozyheavenstay.enums.HotelStatus;
import com.springboot.cozyheavenstay.enums.Role;
import com.springboot.cozyheavenstay.enums.VerificationStatus;
import com.springboot.cozyheavenstay.exception.InvalidCallException;
import com.springboot.cozyheavenstay.exception.ResourceNotFoundException;
import com.springboot.cozyheavenstay.model.Admin;
import com.springboot.cozyheavenstay.model.Hotels;
import com.springboot.cozyheavenstay.model.User;
import com.springboot.cozyheavenstay.repository.AdminRepository;
import com.springboot.cozyheavenstay.repository.BookingRepository;
import com.springboot.cozyheavenstay.repository.BookingRoomsRepository;
import com.springboot.cozyheavenstay.repository.HotelRepository;
import com.springboot.cozyheavenstay.repository.PaymentRepository;
import com.springboot.cozyheavenstay.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {
    @InjectMocks
    private AdminService adminService;
    @Mock
    private AdminRepository adminRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BookingRoomsRepository bookingRoomsRepository;

    @Test
    public void addAdminTest(){
        AdminRequestDto adminRequestDto = new AdminRequestDto("testAdmin","test@gmail.com","testuser","password");

        User user = new User();

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        adminService.addAdmin(adminRequestDto);

        verify(passwordEncoder,times(1)).encode("password");
        verify(userRepository,times(1)).save(any(User.class));
        verify(adminRepository,times(1)).save(any(Admin.class));

        verifyNoMoreInteractions(passwordEncoder);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(adminRepository);
    }

    @Test
    public void updateHotelVerificationStatusTest(){
        Hotels hotel = new Hotels();
        hotel.setId(1L);
        hotel.setVerificationStatus(VerificationStatus.PENDING);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(hotelRepository.save(hotel)).thenReturn(hotel);

        adminService.updateHotelVerificationStatus(1L,VerificationStatus.APPROVED);

        assertEquals(VerificationStatus.APPROVED,hotel.getVerificationStatus());

        verify(hotelRepository,times(1)).findById(1L);
        verify(hotelRepository,times(1)).save(hotel);

        verifyNoMoreInteractions(hotelRepository);
    }

    @Test
    public void updateHotelVerificationStatusTest_Invalid(){
        Hotels hotel = new Hotels();
        hotel.setId(1L);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        assertThrows(InvalidCallException.class,
                ()-> adminService.updateHotelVerificationStatus(1L,VerificationStatus.PENDING));

        verify(hotelRepository,times(1)).findById(1L);
        verify(hotelRepository,times(0)).save(any(Hotels.class));

        verifyNoMoreInteractions(hotelRepository);
    }

    @Test
    public void updateHotelVerificationStatusTest_NotFound(){
        when(hotelRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                ()-> adminService.updateHotelVerificationStatus(5L,VerificationStatus.APPROVED));

        verify(hotelRepository,times(1)).findById(5L);
        verify(hotelRepository,times(0)).save(any(Hotels.class));

        verifyNoMoreInteractions(hotelRepository);
    }

    @Test
    public void updateUserStatusTest(){
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRole(Role.CUSTOMER);
        user.setActive(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        AdminUserResponseDto response = adminService.updateUserStatus(1L,true);

        assertEquals(1L,response.userId());
        assertEquals("testuser",response.username());
        assertEquals(Role.CUSTOMER,response.role());
        assertEquals(true,response.active());

        verify(userRepository,times(1)).findById(1L);
        verify(userRepository,times(1)).save(user);

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void updateUserStatusTest_NotFound(){
        when(userRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                ()-> adminService.updateUserStatus(5L,true));

        verify(userRepository,times(1)).findById(5L);
        verify(userRepository,times(0)).save(any(User.class));

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void updateHotelStatusTest(){
        Hotels hotel = new Hotels();
        hotel.setId(1L);
        hotel.setHotelStatus(HotelStatus.INACTIVE);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(hotelRepository.save(hotel)).thenReturn(hotel);

        adminService.updateHotelStatus(1L,HotelStatus.ACTIVE);

        assertEquals(HotelStatus.ACTIVE,hotel.getHotelStatus());

        verify(hotelRepository,times(1)).findById(1L);
        verify(hotelRepository,times(1)).save(hotel);

        verifyNoMoreInteractions(hotelRepository);
    }

    @Test
    public void updateHotelStatusTest_Invalid(){
        Hotels hotel = new Hotels();
        hotel.setId(1L);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        assertThrows(InvalidCallException.class,
                ()-> adminService.updateHotelStatus(1L,null));

        verify(hotelRepository,times(1)).findById(1L);
        verify(hotelRepository,times(0)).save(any(Hotels.class));

        verifyNoMoreInteractions(hotelRepository);
    }
}
