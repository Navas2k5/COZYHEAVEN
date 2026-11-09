package com.springboot.cozyheavenstay.repository;

import com.springboot.cozyheavenstay.dto.response.CustomerProfileResponseDto;
import com.springboot.cozyheavenstay.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {

    @Query("""
            select c from Customer c
            join c.user u
            where u.username=?1
            """)
    Customer getByUsername(String username);

    @Query("""
        select new com.springboot.cozyheavenstay.dto.response.CustomerProfileResponseDto(
            c.id,
            c.name,
            c.email,
            c.contact,
            c.gender,
            c.address,
            c.user.username
        )
        from Customer c
        where c.user.username = ?1
        """)
    Optional<CustomerProfileResponseDto> findCustomerProfileByUsername(String username);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByContactAndIdNot(String contact, Long id);

}
