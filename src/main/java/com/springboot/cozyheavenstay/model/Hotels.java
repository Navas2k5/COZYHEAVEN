package com.springboot.cozyheavenstay.model;

import com.springboot.cozyheavenstay.enums.HotelStatus;
import com.springboot.cozyheavenstay.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Hotels {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hotel_name",nullable = false)
    private String hotelName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String amenities;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "hotel_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private HotelStatus hotelStatus;

    @Column(name = "verification_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "owner_id",nullable = false)
    private Owner owner;

}
