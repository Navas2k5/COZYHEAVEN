package com.springboot.cozyheavenstay.repository;

import com.springboot.cozyheavenstay.dto.response.BookingResponseDto;
import com.springboot.cozyheavenstay.enums.BookingStatus;
import com.springboot.cozyheavenstay.enums.PaymentStatus;
import com.springboot.cozyheavenstay.model.Bookings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Bookings,Long> {

    long count();

    @Modifying
    @Query("""
            update Bookings b
            set b.bookingStatus=?1
            where b.bookingStatus=?2
            and b.expiresAt<=?3
            """)
    int expirePendingBookings(BookingStatus cancelledStatus, BookingStatus pendingStatus, Instant now);

    @Modifying
    @Query("""
            update Bookings b
            set b.bookingStatus=?1
            where b.id=?2
            and b.bookingStatus=?3
            and b.expiresAt>?4
            """)
    int confirmPendingBooking(BookingStatus confirmedStatus,Long bookingId,BookingStatus pendingStatus,Instant now);

    @Query("""
            select new com.springboot.cozyheavenstay.dto.response.BookingResponseDto(
                b.id,
                b.checkIn,
                b.checkOut,
                b.totalAmount,
                b.bookingStatus,
                b.expiresAt
            ) 
            from Bookings b
            join b.customer c
            join c.user u
            where u.username=?1
            """)
    List<BookingResponseDto> getBookingsByCustomerUsername(String customerUsername, Pageable pageable);

    @Query("""
            select b from Bookings b
            where exists ( select br.id from BookingRooms br where br.booking.id=b.id and br.room.hotel.id=?1)
            order by b.id desc
            """)
    Page<Bookings> getBookingsByHotelId(Long hotelId, Pageable pageable);

    @Query("""
            select b from Bookings b
            where (?1 is null or b.bookingStatus=?1)
            and (?2 is null or exists (select br.id from BookingRooms br where br.booking.id=b.id and br.room.hotel.id=?2))
            order by b.id desc
            """)
    List<Bookings> dynamicFilterBookings(BookingStatus bookingStatus,Long hotelId,Pageable pageable);

    @Modifying
    @Query("""
        update Bookings b
        set b.bookingStatus = ?2
        where b.bookingStatus = ?1
        and b.checkOut < ?3
    """)
    int completeExpiredBookings(
            BookingStatus currentStatus,
            BookingStatus newStatus,
            LocalDate today
    );

    @Query("""
    select b from Bookings b
    where b.bookingStatus = ?1
    and exists (
        select p.id
        from Payments p
        where p.bookings.id = b.id
        and p.paymentStatus = ?2
    )
    and exists (
        select br.id
        from BookingRooms br
        where br.booking.id = b.id
        and br.room.hotel.owner.user.username = ?3
    )
    order by b.id desc
    """)
    List<Bookings> getRefundRequestedBookings(
            BookingStatus bookingStatus,
            PaymentStatus paymentStatus,
            String ownerUsername
    );


}
