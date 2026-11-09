package com.springboot.cozyheavenstay.service;

import com.springboot.cozyheavenstay.dto.request.CustomerRequestDto;
import com.springboot.cozyheavenstay.dto.request.CustomerUpdateRequestDto;
import com.springboot.cozyheavenstay.dto.response.CustomerProfileResponseDto;
import com.springboot.cozyheavenstay.enums.Role;
import com.springboot.cozyheavenstay.exception.InvalidCallException;
import com.springboot.cozyheavenstay.exception.ResourceNotFoundException;
import com.springboot.cozyheavenstay.mapper.CustomerMapper;
import com.springboot.cozyheavenstay.model.Customer;
import com.springboot.cozyheavenstay.model.User;
import com.springboot.cozyheavenstay.repository.CustomerRepository;
import com.springboot.cozyheavenstay.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void addCustomer(CustomerRequestDto customerRequestDto) {

        Customer customer = CustomerMapper.mapDtoToEntity(customerRequestDto);

        //create User object

        User user = new User();
        user.setUsername(customerRequestDto.username());
        user.setPassword(passwordEncoder.encode(customerRequestDto.password()));
        user.setRole(Role.CUSTOMER);
        user = userRepository.save(user);

        customer.setUser(user);

        customerRepository.save(customer);
    }

    public CustomerProfileResponseDto getCustomerProfile(String username) {

        return customerRepository.findCustomerProfileByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer not found"));
    }

    @Transactional
    public CustomerProfileResponseDto updateCustomerProfile(
            String username,
            CustomerUpdateRequestDto dto) {

        Customer customer = customerRepository.getByUsername(username);

        if (customer == null)
            throw new ResourceNotFoundException("Customer not found");

        // Check duplicate email
        if (customerRepository.existsByEmailAndIdNot(dto.email(), customer.getId()))
            throw new InvalidCallException("Email already exists");

        // Check duplicate contact
        if (customerRepository.existsByContactAndIdNot(dto.contact(), customer.getId()))
            throw new InvalidCallException("Contact already exists");

        // Update customer details
        customer.setName(dto.name());
        customer.setEmail(dto.email());
        customer.setContact(dto.contact());
        customer.setGender(dto.gender());
        customer.setAddress(dto.address());

        // Return updated profile
        return new CustomerProfileResponseDto(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getContact(),
                customer.getGender(),
                customer.getAddress(),
                customer.getUser().getUsername()
        );
    }
}
