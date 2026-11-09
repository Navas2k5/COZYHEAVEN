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
import com.springboot.cozyheavenstay.model.Payments;
import com.springboot.cozyheavenstay.repository.BookingRepository;
import com.springboot.cozyheavenstay.repository.BookingRoomsRepository;
import com.springboot.cozyheavenstay.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final BookingRoomsRepository bookingRoomsRepository;

    @Transactional
    public PaymentResponseDto pay(Long bookingId,String customerUsername){
        //find booking
        Bookings booking=bookingRepository.findById(bookingId).orElseThrow(()->
                new ResourceNotFoundException("Booking not found"));

        //check ownership
        if(!booking.getCustomer().getUser().getUsername().equals(customerUsername))
            throw new OwnerShipException("You can pay only for your own bookings");

        //checking booking status
        if(booking.getBookingStatus()!= BookingStatus.PENDING)
            throw new InvalidCallException("Only pending bookings can be paid");

        Instant now = Instant.now();

        //check payment deadline
        if(booking.getExpiresAt()==null || !booking.getExpiresAt().isAfter(now))
            throw new InvalidCallException("Payment window has expired. Create a new booking");

        //confirm only if still pending and unexpired

        int updated = bookingRepository.confirmPendingBooking(BookingStatus.CONFIRMED,bookingId,BookingStatus.PENDING,now);

        if(updated==0)
            throw new InvalidCallException("Booking is no longer available for payment");

        //prepare payment

        Payments payment = new Payments();
        payment.setBookings(bookingRepository.getReferenceById(bookingId));
        payment.setAmount(booking.getTotalAmount());
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaidAt(now);
        payment.setRefundAmount(0.0);

        //save payment
        payment= paymentRepository.save(payment);

        //return response
        return new PaymentResponseDto(payment.getId(),bookingId, payment.getAmount(),payment.getPaymentStatus(),BookingStatus.CONFIRMED,payment.getPaidAt()
        );
    }

    @Transactional
    public RefundResponseDto refundBooking(Long bookingId,String ownerUsername){

        //find booking
        Bookings booking=bookingRepository.findById(bookingId).orElseThrow(()->
                new ResourceNotFoundException("Booking not found"));

        //find rooms linked to bookings
        List<BookingRooms> bookingRooms=bookingRoomsRepository.getRoomsByBookingId(bookingId);
        if(bookingRooms.isEmpty())
            throw new InvalidCallException("No rooms found for this booking");

        //check hotel ownership
        for(BookingRooms bookingRoom: bookingRooms){
            String hotelOwnerUsername=bookingRoom.getRoom().getHotel().getOwner().getUser().getUsername();
            if(!hotelOwnerUsername.equals(ownerUsername))
                throw new OwnerShipException("you can refund only bookings for your hotels");

        }

        //booking must be cancelled
        if(booking.getBookingStatus()!=BookingStatus.CANCELLED)
            throw new InvalidCallException("Only cancelled bookings can be refunded");

        //find payment
        Payments payment  = paymentRepository.getPaymentsByBookingId(bookingId).orElseThrow(()->
                new ResourceNotFoundException("Payment not found"));

        //allow only PAID payments
        if(payment.getPaymentStatus()!=PaymentStatus.PAID)
            throw new InvalidCallException("Only paid payments can be refunded");

        //record full refund
        payment.setRefundAmount(payment.getAmount());
        payment.setRefundedAt(Instant.now());
        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);

        //return refund details
        return new RefundResponseDto(
                payment.getId(),
                booking.getId(),
                payment.getRefundAmount(),
                payment.getPaymentStatus(),
                booking.getBookingStatus(),
                payment.getRefundedAt()
        );
    }
}
