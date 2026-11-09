package com.springboot.cozyheavenstay.model;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class BookingRooms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int adults;

    private int children;

    @Column(name = "price_per_night")
    private Double pricePerNight;

    private Double subtotal;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Bookings booking;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Rooms room;
}
