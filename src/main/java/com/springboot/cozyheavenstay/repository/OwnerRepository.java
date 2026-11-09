package com.springboot.cozyheavenstay.repository;

import com.springboot.cozyheavenstay.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerRepository extends JpaRepository<Owner,Long> {

    @Query("""
            select o from Owner o
            join o.user u
            where u.username=?1
            """)
    Owner getByUsername(String ownerUsername);
}
