package com.springboot.cozyheavenstay.model;

import com.springboot.cozyheavenstay.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false,unique = true)
    private String contact;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String address;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
