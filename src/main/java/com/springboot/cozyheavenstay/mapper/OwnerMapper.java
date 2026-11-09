package com.springboot.cozyheavenstay.mapper;

import com.springboot.cozyheavenstay.dto.request.OwnerRequestDto;
import com.springboot.cozyheavenstay.model.Owner;
import org.springframework.stereotype.Component;

@Component
public class OwnerMapper {

    public static Owner convertDtoToEntity(OwnerRequestDto dto){
        Owner owner = new Owner();
        owner.setName(dto.name());
        owner.setEmail(dto.email());
        owner.setContact(dto.contact());
        owner.setGender(dto.gender());
        owner.setAddress(dto.address());
        owner.setBusinessName(dto.businessName());
        return owner;
    }
}
