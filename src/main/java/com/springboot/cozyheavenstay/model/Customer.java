package com.springboot.cozyheavenstay.model;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "customers")
public class Customer {

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

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
