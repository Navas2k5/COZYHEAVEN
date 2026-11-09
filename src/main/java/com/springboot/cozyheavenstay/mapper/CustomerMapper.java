package com.springboot.cozyheavenstay.mapper;

import com.springboot.cozyheavenstay.dto.request.CustomerRequestDto;
import com.springboot.cozyheavenstay.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public static Customer mapDtoToEntity(CustomerRequestDto dto){
        Customer customer = new Customer();
        customer.setName(dto.name());
        customer.setEmail(dto.email());
        customer.setContact(dto.contact());
        customer.setGender(dto.gender());
        customer.setAddress(dto.address());
        return customer;
    }
}
