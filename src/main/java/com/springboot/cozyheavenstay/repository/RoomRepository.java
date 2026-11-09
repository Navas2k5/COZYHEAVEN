package com.springboot.cozyheavenstay.repository;

import com.springboot.cozyheavenstay.dto.response.RoomResponseDto;
import com.springboot.cozyheavenstay.enums.BedType;
import com.springboot.cozyheavenstay.enums.BookingStatus;
import com.springboot.cozyheavenstay.enums.RoomStatus;
import com.springboot.cozyheavenstay.model.Bookings;
import com.springboot.cozyheavenstay.model.Rooms;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Rooms,Long> {

    @Query("""
            select count(r.id) from Rooms r
            where r.hotel.id = ?1
            and r.roomNumber=?2
            """)
    long countByHotelAndRoomNumber(Long hotelId, String roomNumber);

    @Query("""
        select r from Rooms r
        where r.hotel.id = ?1
        order by r.id desc
        """)
    Page<Rooms> getRoomsByHotelId(Long hotelId, Pageable pageable);

    @Query("""
            select new com.springboot.cozyheavenstay.dto.response.RoomResponseDto(
                r.id,
                r.roomNumber,
                r.roomSize,
                r.bedType,
                r.maxOccupancy,
                r.baseFare,
                r.ac,
                r.roomStatus
            ) 
            from Rooms r
            where r.hotel.id=?1
            and r.roomStatus=?2
            and (?3 is null or r.bedType = ?3)
            and (?4 is null or r.ac=?4)
            and (?5 is null or r.baseFare>= ?5)
            and (?6 is null or r.baseFare<= ?6)
            and (?7 is null or r.maxOccupancy >= ?7)
            and not exists (
                select br.id from BookingRooms br
                join br.booking b
                where br.room.id=r.id
                and b.bookingStatus in (?10)
                and b.checkIn < ?9
                and b.checkOut > ?8
            )
            order by r.baseFare asc, r.id asc
            """)
    List<RoomResponseDto> dynamicFilterRooms(Long hotelId,
                                             RoomStatus roomStatus,
                                             BedType bedType,
                                             Boolean ac,
                                             Double minFare,
                                             Double maxFare,
                                             Integer Guests,
                                             LocalDate checkIn,
                                             LocalDate checkOut,
                                             List<BookingStatus> bookingStatuses,
                                             Pageable pageable
    );

    @Query("""
        select new com.springboot.cozyheavenstay.dto.response.RoomResponseDto(
            r.id,
            r.roomNumber,
            r.roomSize,
            r.bedType,
            r.maxOccupancy,
            r.baseFare,
            r.ac,
            r.roomStatus
        )
        from Rooms r
        where r.hotel.id = ?1
        and r.roomStatus = ?2
        and (?3 is null or r.bedType = ?3)
        and (?4 is null or r.ac = ?4)
        and (?5 is null or r.baseFare >= ?5)
        and (?6 is null or r.baseFare <= ?6)
        and (?7 is null or r.maxOccupancy >= ?7)
        and not exists (
            select br.id from BookingRooms br
            join br.booking b
            where br.room.id = r.id
            and b.bookingStatus in (?10)
            and b.checkIn < ?9
            and b.checkOut > ?8
        )
        order by r.baseFare asc, r.id asc
        """)
    List<RoomResponseDto> getAvailableRoomsForGrouping(
            Long hotelId,
            RoomStatus roomStatus,
            BedType bedType,
            Boolean ac,
            Double minFare,
            Double maxFare,
            Integer guests,
            LocalDate checkIn,
            LocalDate checkOut,
            List<BookingStatus> bookingStatuses
    );
    Long countByHotelIdAndRoomStatus(Long hotelId, RoomStatus roomStatus);

    @Query("""
        select count(r.id) from Rooms r
        where r.hotel.id = ?1
        """)
    Long countTotalRoomsByHotelId(Long hotelId);

    @Query("""
        select count(r.id) from Rooms r
        where r.hotel.id = ?1
        and r.roomStatus = ?2
        """)
    Long countRoomsByHotelIdAndStatus(Long hotelId, RoomStatus roomStatus);
}
