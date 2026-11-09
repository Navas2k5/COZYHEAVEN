
package com.springboot.cozyheavenstay.service;

import com.springboot.cozyheavenstay.dto.response.PaymentResponseDto;
import com.springboot.cozyheavenstay.dto.response.RefundResponseDto;
import com.springboot.cozyheavenstay.enums.BookingStatus;
import com.springboot.cozyheavenstay.enums.PaymentStatus;
import com.springboot.cozyheavenstay.exception.InvalidCallException;
import com.springboot.cozyheavenstay.exception.OwnerShipException;
import com.springboot.cozyheavenstay.exception.ResourceNotFoundException;
import com.springboot.cozyheavenstay.model.BookingRooms;
import com.springboot.cozyheavenstay.model.Bookings;
import com.springboot.cozyheavenstay.model.Customer;
import com.springboot.cozyheavenstay.model.Hotels;
import com.springboot.cozyheavenstay.model.Owner;
import com.springboot.cozyheavenstay.model.Payments;
import com.springboot.cozyheavenstay.model.Rooms;
import com.springboot.cozyheavenstay.model.User;
import com.springboot.cozyheavenstay.repository.BookingRepository;
import com.springboot.cozyheavenstay.repository.BookingRoomsRepository;
import com.springboot.cozyheavenstay.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @InjectMocks
    private PaymentService paymentService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingRoomsRepository bookingRoomsRepository;

    @Test
    public void payTest(){
        User user = new User();
        user.setUsername("testcustomer");

        Customer customer = new Customer();
        customer.setUser(user);

        Bookings booking = new Bookings();
        booking.setId(1L);
        booking.setCustomer(customer);
        booking.setTotalAmount(4000.0);
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setExpiresAt(Instant.now().plusSeconds(600));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.confirmPendingBooking(
                eq(BookingStatus.CONFIRMED),
                eq(1L),
                eq(BookingStatus.PENDING),
                any(Instant.class)
        )).thenReturn(1);
        when(bookingRepository.getReferenceById(1L)).thenReturn(booking);
        when(paymentRepository.save(any(Payments.class))).thenAnswer(invocation -> {
            Payments payment = invocation.getArgument(0);
            payment.setId(1L);
            return payment;
        });

        PaymentResponseDto response = paymentService.pay(1L,"testcustomer");

        assertNotNull(response);
        assertEquals(1L,response.paymentId());
        assertEquals(1L,response.bookingId());
        assertEquals(4000.0,response.amount());
        assertEquals(PaymentStatus.PAID,response.paymentStatus());
        assertEquals(BookingStatus.CONFIRMED,response.bookingStatus());

        verify(bookingRepository,times(1)).findById(1L);
        verify(bookingRepository,times(1)).confirmPendingBooking(
                eq(BookingStatus.CONFIRMED),
                eq(1L),
                eq(BookingStatus.PENDING),
                any(Instant.class)
        );
        verify(bookingRepository,times(1)).getReferenceById(1L);
        verify(paymentRepository,times(1)).save(any(Payments.class));

        verifyNoMoreInteractions(bookingRepository);
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    public void payTest_NotFound(){
        when(bookingRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                ()-> paymentService.pay(5L,"testcustomer"));

        verify(bookingRepository,times(1)).findById(5L);

        verifyNoMoreInteractions(bookingRepository);
        verifyNoInteractions(paymentRepository);
    }

    @Test
    public void payTest_OwnerShipException(){
        User user = new User();
        user.setUsername("anothercustomer");

        Customer customer = new Customer();
        customer.setUser(user);

        Bookings booking = new Bookings();
        booking.setId(1L);
        booking.setCustomer(customer);
        booking.setBookingStatus(BookingStatus.PENDING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(OwnerShipException.class,
                ()-> paymentService.pay(1L,"testcustomer"));

        verify(bookingRepository,times(1)).findById(1L);

        verifyNoMoreInteractions(bookingRepository);
        verifyNoInteractions(paymentRepository);
    }

    @Test
    public void payTest_ExpiredBooking(){
        User user = new User();
        user.setUsername("testcustomer");

        Customer customer = new Customer();
        customer.setUser(user);

        Bookings booking = new Bookings();
        booking.setId(1L);
        booking.setCustomer(customer);
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setExpiresAt(Instant.now().minusSeconds(60));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(InvalidCallException.class,
                ()-> paymentService.pay(1L,"testcustomer"));

        verify(bookingRepository,times(1)).findById(1L);
        verifyNoMoreInteractions(bookingRepository);
        verifyNoInteractions(paymentRepository);
    }

    @Test
    public void refundBookingTest(){
        User user = new User();
        user.setUsername("testowner");

        Owner owner = new Owner();
        owner.setUser(user);

        Hotels hotel = new Hotels();
        hotel.setOwner(owner);

        Rooms room = new Rooms();
        room.setHotel(hotel);

        BookingRooms bookingRoom = new BookingRooms();
        bookingRoom.setRoom(room);

        Bookings booking = new Bookings();
        booking.setId(1L);
        booking.setBookingStatus(BookingStatus.CANCELLED);

        Payments payment = new Payments();
        payment.setId(1L);
        payment.setAmount(4000.0);
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setRefundAmount(0.0);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRoomsRepository.getRoomsByBookingId(1L))
                .thenReturn(List.of(bookingRoom));
        when(paymentRepository.getPaymentsByBookingId(1L))
                .thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);

        RefundResponseDto response = paymentService.refundBooking(1L,"testowner");

        assertNotNull(response);
        assertEquals(1L,response.paymentId());
        assertEquals(1L,response.bookingId());
        assertEquals(4000.0,response.refundAmount());
        assertEquals(PaymentStatus.REFUNDED,response.paymentStatus());
        assertEquals(BookingStatus.CANCELLED,response.bookingStatus());

        verify(bookingRepository,times(1)).findById(1L);
        verify(bookingRoomsRepository,times(1)).getRoomsByBookingId(1L);
        verify(paymentRepository,times(1)).getPaymentsByBookingId(1L);
        verify(paymentRepository,times(1)).save(payment);

        verifyNoMoreInteractions(bookingRepository);
        verifyNoMoreInteractions(bookingRoomsRepository);
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    public void refundBookingTest_NoRoomsFound(){
        Bookings booking = new Bookings();
        booking.setId(1L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRoomsRepository.getRoomsByBookingId(1L))
                .thenReturn(Collections.emptyList());

        assertThrows(InvalidCallException.class,
                ()-> paymentService.refundBooking(1L,"testowner"));

        verify(bookingRepository,times(1)).findById(1L);
        verify(bookingRoomsRepository,times(1)).getRoomsByBookingId(1L);

        verifyNoMoreInteractions(bookingRepository);
        verifyNoMoreInteractions(bookingRoomsRepository);
        verifyNoInteractions(paymentRepository);
    }

    @Test
    public void refundBookingTest_OwnerShipException(){
        User user = new User();
        user.setUsername("anotherowner");

        Owner owner = new Owner();
        owner.setUser(user);

        Hotels hotel = new Hotels();
        hotel.setOwner(owner);

        Rooms room = new Rooms();
        room.setHotel(hotel);

        BookingRooms bookingRoom = new BookingRooms();
        bookingRoom.setRoom(room);

        Bookings booking = new Bookings();
        booking.setId(1L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRoomsRepository.getRoomsByBookingId(1L))
                .thenReturn(List.of(bookingRoom));

        assertThrows(OwnerShipException.class,
                ()-> paymentService.refundBooking(1L,"testowner"));

        verify(bookingRepository,times(1)).findById(1L);
        verify(bookingRoomsRepository,times(1)).getRoomsByBookingId(1L);

        verifyNoMoreInteractions(bookingRepository);
        verifyNoMoreInteractions(bookingRoomsRepository);
        verifyNoInteractions(paymentRepository);
    }
}
