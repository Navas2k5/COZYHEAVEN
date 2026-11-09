package com.springboot.cozyheavenstay.model;

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
public class Reviews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;

    private String comment;

    @Column(name = "created_at")
    private Instant createdAt;

    @OneToOne
    @JoinColumn(name="booking_id",nullable = false,unique = true)
    private Bookings booking;
}
