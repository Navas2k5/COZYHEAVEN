package com.springboot.cozyheavenstay.model;

import com.springboot.cozyheavenstay.enums.PaymentStatus;
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
public class Payments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name="refund_amount")
    private Double refundAmount;

    @Column(name = "refunded_at")
    private Instant refundedAt;

    @OneToOne
    @JoinColumn(name = "booking_id",nullable = false, unique = true)
    private Bookings bookings;
}
