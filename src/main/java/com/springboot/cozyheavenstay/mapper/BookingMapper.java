package com.springboot.cozyheavenstay.mapper;

import com.springboot.cozyheavenstay.dto.response.AdminBookingResponseDto;
import com.springboot.cozyheavenstay.dto.response.BookingResponseDto;
import com.springboot.cozyheavenstay.dto.response.BookingRoomDetailsDto;
import com.springboot.cozyheavenstay.dto.response.OwnerBookingResponseDto;
import com.springboot.cozyheavenstay.model.BookingRooms;
import com.springboot.cozyheavenstay.model.Bookings;
import com.springboot.cozyheavenstay.model.Hotels;
import com.springboot.cozyheavenstay.model.Payments;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingMapper {

    public static BookingResponseDto convertEntityToDto(Bookings bookings){
        return new BookingResponseDto(
                bookings.getId(),
                bookings.getCheckIn(),
                bookings.getCheckOut(),
                bookings.getTotalAmount(),
                bookings.getBookingStatus(),
                bookings.getExpiresAt()
        );
    }

    public static BookingRoomDetailsDto convertBookingRoomToDto(BookingRooms bookingRooms){
        return new BookingRoomDetailsDto(
                bookingRooms.getBooking().getId(),
                bookingRooms.getRoom().getRoomNumber(),
                bookingRooms.getAdults(),
                bookingRooms.getChildren(),
                bookingRooms.getPricePerNight(),
                bookingRooms.getSubtotal()
        );
    }

    public static OwnerBookingResponseDto convertEntityToOwnerDto(Bookings bookings, List<BookingRoomDetailsDto> rooms, Payments payment){
        return new OwnerBookingResponseDto(
                bookings.getId(),
                bookings.getCustomer().getName(),
                bookings.getCheckIn(),
                bookings.getCheckOut(),
                bookings.getBookedAt(),
                bookings.getBookingStatus(),
                bookings.getTotalAmount(),
                rooms,
                payment == null? null:payment.getPaymentStatus(),
                payment==null?null: payment.getRefundAmount(),
                payment==null?null:payment.getRefundedAt()
        );
    }

    public static AdminBookingResponseDto convertEntityToAdminDto(Bookings bookings, Hotels hotels,List<BookingRoomDetailsDto> rooms,Payments payments){
        return new AdminBookingResponseDto(
                bookings.getId(),
                hotels.getId(),
                hotels.getHotelName(),
                bookings.getCustomer().getName(),
                bookings.getCheckIn(),
                bookings.getCheckOut(),
                bookings.getBookedAt(),
                bookings.getBookingStatus(),
                bookings.getTotalAmount(),
                rooms,
                payments==null?null:payments.getPaymentStatus(),
                payments==null?null: payments.getRefundAmount(),
                payments==null?null:payments.getRefundedAt()
        );
    }
}
