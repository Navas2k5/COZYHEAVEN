package com.springboot.cozyheavenstay.repository;

import com.springboot.cozyheavenstay.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Long> {
}
