package com.springboot.cozyheavenstay.repository;

import com.springboot.cozyheavenstay.dto.response.PublicReviewResponseDto;
import com.springboot.cozyheavenstay.model.Reviews;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Reviews,Long> {

    @Query("""
            select count(r.id) from Reviews r
            where r.booking.id=?1 
            """)
    long countByBookingId(Long bookingId);

    @Query("""
            select new com.springboot.cozyheavenstay.dto.response.PublicReviewResponseDto(
                r.booking.customer.name,
                r.rating,
                r.comment,
                r.createdAt
            )
            from Reviews r
            where exists ( select br.id from BookingRooms br where br.booking.id=r.booking.id and br.room.hotel.id=?1 )
            order by r.createdAt desc, r.id desc
            """)
    List<PublicReviewResponseDto> getReviewsByHotelId(Long hotelId, Pageable pageable);

    @Query("""
            select avg(r.rating) from Reviews r
            where exists ( select br.id from BookingRooms br where br.booking.id=r.booking.id and br.room.hotel.id=?1 )
            """)
    Double getAverageRatingByHotelId(Long hotelId);

    @Query("""
            select count(r.id) from Reviews r
            where exists ( select br.id from BookingRooms br where br.booking.id=r.booking.id and br.room.hotel.id=?1 )
            """)
    Long countReviewsByHotelId(Long hotelId);

    Optional<Reviews> findByBookingId(Long bookingId);

}
