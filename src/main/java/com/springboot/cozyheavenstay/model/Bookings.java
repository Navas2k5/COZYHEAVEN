package com.springboot.cozyheavenstay.model;

import com.springboot.cozyheavenstay.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Bookings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "check_in")
    private LocalDate checkIn;

    @Column(name = "check_out")
    private LocalDate checkOut;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status")
    private BookingStatus bookingStatus;

    @Column(name = "booked_at")
    private Instant bookedAt;

    @Column(name = "expiresAt")
    private Instant expiresAt;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
