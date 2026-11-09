package com.springboot.cozyheavenstay.repository;

import com.springboot.cozyheavenstay.model.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payments,Long> {

    @Query("""
            select p from Payments p
            where p.bookings.id=?1
            """)
    Optional<Payments> getPaymentsByBookingId(Long bookingId);
}
