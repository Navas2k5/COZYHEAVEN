package com.springboot.cozyheavenstay.repository;

import com.springboot.cozyheavenstay.enums.BookingStatus;
import com.springboot.cozyheavenstay.model.BookingRooms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRoomsRepository extends JpaRepository<BookingRooms,Long> {

    @Query("""
            select count(br.id) from BookingRooms br
            join br.booking b
            where br.room.id=?1
            and b.bookingStatus in (?2)
            and b.checkIn<?4
            and b.checkOut>?3
            """)
    long countOverlappingBookings(Long roomId, List<BookingStatus> bookingStatuses, LocalDate checkIn, LocalDate checkOut);

    @Query("""
            select br from BookingRooms br
            where br.booking.id=?1
            """)
    List<BookingRooms> getRoomsByBookingId(Long bookingId);
}
