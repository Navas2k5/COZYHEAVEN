package com.springboot.cozyheavenstay.model;

import com.springboot.cozyheavenstay.enums.BedType;
import com.springboot.cozyheavenstay.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Rooms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_number")
    private String roomNumber;

    @Column(name = "room_size")
    private Double roomSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "bed_type")
    private BedType bedType;

    @Column(name = "max_occupancy")
    private Integer maxOccupancy;

    @Column(name = "base_fare",nullable = false)
    private Double baseFare;

    @Column(name = "is_ac",nullable = false)
    private Boolean ac;

    @Column(name = "room_status",nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomStatus roomStatus;

    @ManyToOne
    @JoinColumn(name = "hotel_id",nullable = false)
    private Hotels hotel;
}
