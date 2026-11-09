package com.springboot.cozyheavenstay.repository;

import com.springboot.cozyheavenstay.dto.response.AdminUserResponseDto;
import com.springboot.cozyheavenstay.dto.response.UserDetailsDto;
import com.springboot.cozyheavenstay.enums.Role;
import com.springboot.cozyheavenstay.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    @Query("""
            select u from User u
            where u.username=?1 and u.active=?2
            """)
    User loadUserByUsername(String username,boolean active);

    @Query("""
            select new com.springboot.cozyheavenstay.dto.response.AdminUserResponseDto(
                u.id,
                u.username,
                u.role,
                u.active
            )
            from User u
            where u.role in (?1)
            and (?2 is null or u.role=?2)
            order by u.id desc
            """)
    List<AdminUserResponseDto> getUserForAdmin(List<Role> allowedRoles,
                                               Role role,
                                               Pageable pageable);

    @Query("""
            select new com.springboot.cozyheavenstay.dto.response.UserDetailsDto(
                u.username,u.active,u.role
            )
            from User u
            where u.username=?1
            """)
    UserDetailsDto getUserDetails(String loggedInUsername);

    long count();

    long countByRole(Role role);
}
