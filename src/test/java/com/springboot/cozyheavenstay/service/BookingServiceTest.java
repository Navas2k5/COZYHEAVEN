
package com.springboot.cozyheavenstay.service;

import com.springboot.cozyheavenstay.dto.request.BookingQuoteRequestDto;
import com.springboot.cozyheavenstay.dto.request.BookingRoomRequestDto;
import com.springboot.cozyheavenstay.dto.response.BookingQuoteResponseDto;
import com.springboot.cozyheavenstay.dto.response.BookingResponseDto;
import com.springboot.cozyheavenstay.enums.BedType;
import com.springboot.cozyheavenstay.enums.BookingStatus;
import com.springboot.cozyheavenstay.enums.HotelStatus;
import com.springboot.cozyheavenstay.enums.RoomStatus;
import com.springboot.cozyheavenstay.enums.VerificationStatus;
import com.springboot.cozyheavenstay.exception.InvalidCallException;
import com.springboot.cozyheavenstay.exception.OwnerShipException;
import com.springboot.cozyheavenstay.exception.ResourceNotFoundException;
import com.springboot.cozyheavenstay.model.Bookings;
import com.springboot.cozyheavenstay.model.Customer;
import com.springboot.cozyheavenstay.model.Hotels;
import com.springboot.cozyheavenstay.model.Rooms;
import com.springboot.cozyheavenstay.model.User;
import com.springboot.cozyheavenstay.repository.RoomRepository;
import com.springboot.cozyheavenstay.repository.BookingRoomsRepository;
import com.springboot.cozyheavenstay.repository.CustomerRepository;
import com.springboot.cozyheavenstay.repository.BookingRepository;
import com.springboot.cozyheavenstay.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @InjectMocks
    private BookingService bookingService;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private BookingRoomsRepository bookingRoomsRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private PaymentRepository paymentRepository;

    @Test
    public void getQuoteTest(){
        LocalDate checkIn = LocalDate.now().plusDays(2);
        LocalDate checkOut = LocalDate.now().plusDays(4);

        Hotels hotel = new Hotels();
        hotel.setId(1L);
        hotel.setVerificationStatus(VerificationStatus.APPROVED);
        hotel.setHotelStatus(HotelStatus.ACTIVE);

        Rooms room = new Rooms();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setBedType(BedType.DOUBLE);
        room.setMaxOccupancy(4);
        room.setBaseFare(2000.0);
        room.setRoomStatus(RoomStatus.ACTIVE);
        room.setHotel(hotel);

        BookingRoomRequestDto roomRequest = new BookingRoomRequestDto(1L,2,0);

        BookingQuoteRequestDto dto = new BookingQuoteRequestDto(
                checkIn,
                checkOut,
                List.of(roomRequest)
        );

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRoomsRepository.countOverlappingBookings(
                eq(1L),anyList(),eq(checkIn),eq(checkOut)
        )).thenReturn(0L);

        BookingQuoteResponseDto response = bookingService.getQuote(dto);

        assertNotNull(response);
        assertEquals(1L,response.hotelId());
        assertEquals(2L,response.nights());
        assertEquals(4000.0,response.totalAmount());
        assertEquals(1,response.rooms().size());

        verify(roomRepository,times(1)).findById(1L);
        verify(bookingRoomsRepository,times(1)).countOverlappingBookings(
                eq(1L),anyList(),eq(checkIn),eq(checkOut)
        );

        verifyNoMoreInteractions(roomRepository);
        verifyNoMoreInteractions(bookingRoomsRepository);
    }

    @Test
    public void getQuoteTest_DuplicateRoom(){
        LocalDate checkIn = LocalDate.now().plusDays(2);
        LocalDate checkOut = LocalDate.now().plusDays(4);

        Hotels hotel = new Hotels();
        hotel.setId(1L);
        hotel.setVerificationStatus(VerificationStatus.APPROVED);
        hotel.setHotelStatus(HotelStatus.ACTIVE);

        Rooms room = new Rooms();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setBedType(BedType.DOUBLE);
        room.setMaxOccupancy(4);
        room.setBaseFare(2000.0);
        room.setRoomStatus(RoomStatus.ACTIVE);
        room.setHotel(hotel);

        BookingRoomRequestDto roomRequest = new BookingRoomRequestDto(1L,2,0);

        BookingQuoteRequestDto dto = new BookingQuoteRequestDto(
                checkIn,
                checkOut,
                List.of(roomRequest,roomRequest)
        );

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRoomsRepository.countOverlappingBookings(
                eq(1L),anyList(),eq(checkIn),eq(checkOut)
        )).thenReturn(0L);

        assertThrows(InvalidCallException.class,
                ()-> bookingService.getQuote(dto));

        verify(roomRepository,times(1)).findById(1L);
        verify(bookingRoomsRepository,times(1)).countOverlappingBookings(
                eq(1L),anyList(),eq(checkIn),eq(checkOut)
        );

        verifyNoMoreInteractions(roomRepository);
        verifyNoMoreInteractions(bookingRoomsRepository);
    }

    @Test
    public void addBookingTest(){
        LocalDate checkIn = LocalDate.now().plusDays(2);
        LocalDate checkOut = LocalDate.now().plusDays(4);

        User user = new User();
        user.setUsername("testcustomer");

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUser(user);

        Hotels hotel = new Hotels();
        hotel.setId(1L);
        hotel.setVerificationStatus(VerificationStatus.APPROVED);
        hotel.setHotelStatus(HotelStatus.ACTIVE);

        Rooms room = new Rooms();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setBedType(BedType.DOUBLE);
        room.setMaxOccupancy(4);
        room.setBaseFare(2000.0);
        room.setRoomStatus(RoomStatus.ACTIVE);
        room.setHotel(hotel);

        BookingQuoteRequestDto dto = new BookingQuoteRequestDto(
                checkIn,
                checkOut,
                List.of(new BookingRoomRequestDto(1L,2,0))
        );

        when(customerRepository.getByUsername("testcustomer")).thenReturn(customer);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRoomsRepository.countOverlappingBookings(
                eq(1L),anyList(),eq(checkIn),eq(checkOut)
        )).thenReturn(0L);
        when(bookingRepository.save(any(Bookings.class))).thenAnswer(invocation -> {
            Bookings booking = invocation.getArgument(0);
            booking.setId(1L);
            return booking;
        });

        BookingResponseDto response = bookingService.addBooking(dto,"testcustomer");

        assertNotNull(response);
        assertEquals(1L,response.bookingId());
        assertEquals(BookingStatus.PENDING,response.bookingStatus());
        assertEquals(4000.0,response.totalAmount());

        verify(customerRepository,times(1)).getByUsername("testcustomer");
        verify(roomRepository,times(2)).findById(1L);
        verify(bookingRoomsRepository,times(1)).countOverlappingBookings(
                eq(1L),anyList(),eq(checkIn),eq(checkOut)
        );
        verify(bookingRepository,times(1)).save(any(Bookings.class));
        verify(bookingRoomsRepository,times(1)).save(any());

        verifyNoMoreInteractions(customerRepository);
        verifyNoMoreInteractions(roomRepository);
        verifyNoMoreInteractions(bookingRepository);
        verifyNoMoreInteractions(bookingRoomsRepository);
    }

    @Test
    public void addBookingTest_CustomerNotFound(){
        LocalDate checkIn = LocalDate.now().plusDays(2);
        LocalDate checkOut = LocalDate.now().plusDays(4);

        BookingQuoteRequestDto dto = new BookingQuoteRequestDto(
                checkIn,
                checkOut,
                List.of(new BookingRoomRequestDto(1L,2,0))
        );

        when(customerRepository.getByUsername("wronguser")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                ()-> bookingService.addBooking(dto,"wronguser"));

        verify(customerRepository,times(1)).getByUsername("wronguser");

        verifyNoMoreInteractions(customerRepository);
        verifyNoInteractions(roomRepository);
        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(bookingRoomsRepository);
    }

    @Test
    public void cancelBookingTest(){
        LocalDate checkIn = LocalDate.now().plusDays(2);

        User user = new User();
        user.setUsername("testcustomer");

        Customer customer = new Customer();
        customer.setUser(user);

        Bookings booking = new Bookings();
        booking.setId(1L);
        booking.setCustomer(customer);
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkIn.plusDays(2));
        booking.setTotalAmount(4000.0);
        booking.setBookingStatus(BookingStatus.PENDING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingResponseDto response = bookingService.cancelBooking(1L,"testcustomer");

        assertNotNull(response);
        assertEquals(BookingStatus.CANCELLED,response.bookingStatus());
        assertEquals(1L,response.bookingId());

        verify(bookingRepository,times(1)).findById(1L);
        verify(bookingRepository,times(1)).save(booking);

        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    public void cancelBookingTest_OwnerShipException(){
        User user = new User();
        user.setUsername("anothercustomer");

        Customer customer = new Customer();
        customer.setUser(user);

        Bookings booking = new Bookings();
        booking.setId(1L);
        booking.setCustomer(customer);
        booking.setCheckIn(LocalDate.now().plusDays(2));
        booking.setBookingStatus(BookingStatus.PENDING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(OwnerShipException.class,
                ()-> bookingService.cancelBooking(1L,"testcustomer"));

        verify(bookingRepository,times(1)).findById(1L);
        verify(bookingRepository,times(0)).save(any(Bookings.class));

        verifyNoMoreInteractions(bookingRepository);
    }
}
