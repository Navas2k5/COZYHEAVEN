package com.springboot.cozyheavenstay.controller;

import com.springboot.cozyheavenstay.dto.request.CustomerRequestDto;
import com.springboot.cozyheavenstay.dto.request.CustomerUpdateRequestDto;
import com.springboot.cozyheavenstay.dto.response.CustomerProfileResponseDto;
import com.springboot.cozyheavenstay.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173/")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/api/customer/add")
    public void addCustomer(@Valid @RequestBody CustomerRequestDto customerRequestDto){
        customerService.addCustomer(customerRequestDto);
        log.info("Customer registered successfully. Username: {}",
                customerRequestDto.username());
    }

    @GetMapping("/api/customer/profile")
    public CustomerProfileResponseDto getCustomerProfile(Principal principal) {

        return customerService.getCustomerProfile(principal.getName());
    }

    @PutMapping("/api/customer/profile")
    public CustomerProfileResponseDto updateCustomerProfile(
            @Valid @RequestBody CustomerUpdateRequestDto dto,
            Principal principal) {



        return customerService.updateCustomerProfile(
                principal.getName(),
                dto
        );
    }
}
