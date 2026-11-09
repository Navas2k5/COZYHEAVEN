package com.springboot.cozyheavenstay.repository;

import com.springboot.cozyheavenstay.dto.response.CustomerHotelResponseDto;
import com.springboot.cozyheavenstay.enums.BookingStatus;
import com.springboot.cozyheavenstay.enums.HotelStatus;
import com.springboot.cozyheavenstay.enums.RoomStatus;
import com.springboot.cozyheavenstay.enums.VerificationStatus;
import com.springboot.cozyheavenstay.model.Hotels;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotels,Long> {

    @Query("""
            select h from Hotels h
            join h.owner o
            join o.user u
            where u.username=?1
            """)
    List<Hotels> getHotelByOwnerUsername(String ownerUsername);

    @Query("""
            select h from Hotels h
            join h.owner o
            where h.verificationStatus=?1
            """)
    List<Hotels> getAllPendingHotels(VerificationStatus verificationStatus);

    @Query("""
        select new com.springboot.cozyheavenstay.dto.response.CustomerHotelResponseDto(
            h.id,
            h.hotelName,
            h.description,
            h.location,
            h.address,
            h.amenities,
            h.imageUrl
        )
        from Hotels h
        where h.verificationStatus = ?1
        AND h.hotelStatus = ?2
        AND (?3 IS NULL OR h.location = ?3)
        AND (?4 IS NULL OR h.hotelName = ?4)
        AND (?5 IS NULL OR EXISTS ( 
                select r.id from Rooms r
                where r.hotel=h
                AND r.roomStatus=?7
                AND NOT EXISTS (
                    select br.id from BookingRooms br
                    join br.booking b
                    where br.room = r
                    AND b.bookingStatus IN (?8,?9)
                    AND b.checkIn<?6
                    AND b.checkOut > ?5
                )
            )
        )
        order by h.id desc
        """)
    Page<CustomerHotelResponseDto> getAvailableHotels(
            VerificationStatus verificationStatus,
            HotelStatus hotelStatus,
            String location,
            String hotelName,
            LocalDate checkIn,
            LocalDate checkOut,
            RoomStatus roomStatus,
            BookingStatus pending,
            BookingStatus confirmed,
            Pageable pageable
    );

    @Query("""
        select h from Hotels h
        join h.owner o
        join o.user u
        where h.id=?1
        AND u.username=?2
        """)
    Hotels getHotelByIdAndOwnerUsername(Long hotelId, String ownerUsername);

    long count();

    long countByVerificationStatus(VerificationStatus verificationStatus);

    long countByHotelStatus(HotelStatus hotelStatus);
}
