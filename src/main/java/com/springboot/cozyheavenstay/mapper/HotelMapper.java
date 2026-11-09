package com.springboot.cozyheavenstay.mapper;

import com.springboot.cozyheavenstay.dto.request.HotelRequestDto;
import com.springboot.cozyheavenstay.dto.response.AdminHotelResponseDto;
import com.springboot.cozyheavenstay.dto.response.CustomerHotelResponseDto;
import com.springboot.cozyheavenstay.dto.response.HotelResponseDto;
import com.springboot.cozyheavenstay.model.Hotels;
import com.springboot.cozyheavenstay.model.Owner;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

@Component
public class HotelMapper {

    public static Hotels convertDtoToEntity(HotelRequestDto dto){
        Hotels hotel=new Hotels();
        hotel.setHotelName(dto.hotelName());
        hotel.setDescription(dto.description());
        hotel.setLocation(dto.location());
        hotel.setAddress(dto.address());
        hotel.setAmenities(dto.amenities());
        return hotel;
    }


    public static HotelResponseDto convertEntityToDto(Hotels hotels) {
        return new HotelResponseDto(
                hotels.getId(),
                hotels.getHotelName(),
                hotels.getDescription(),
                hotels.getLocation(),
                hotels.getAddress(),
                hotels.getAmenities(),
                hotels.getImageUrl(),
                hotels.getHotelStatus(),
                hotels.getVerificationStatus(),
                hotels.getCreatedAt()
        );
    }

    public static AdminHotelResponseDto convertEntityToAdminDto(Hotels hotel){
        Owner owner = hotel.getOwner();

        return new AdminHotelResponseDto(
                hotel.getId(),
                hotel.getHotelName(),
                hotel.getDescription(),
                hotel.getLocation(),
                hotel.getAddress(),
                hotel.getAmenities(),
                hotel.getImageUrl(),
                hotel.getHotelStatus(),
                hotel.getVerificationStatus(),
                hotel.getCreatedAt(),
                owner.getId(),
                owner.getName(),
                owner.getBusinessName(),
                owner.getEmail(),
                owner.getContact()
        );
    }

    public static void updateEntityFromDto(HotelRequestDto dto, Hotels hotel) {
        hotel.setHotelName(dto.hotelName());
        hotel.setDescription(dto.description());
        hotel.setLocation(dto.location());
        hotel.setAddress(dto.address());
        hotel.setAmenities(dto.amenities());
    }

    public static CustomerHotelResponseDto convertEntityToCustomerHotelResponseDto(Hotels hotel){
        return new CustomerHotelResponseDto(
                hotel.getId(),
                hotel.getHotelName(),
                hotel.getDescription(),
                hotel.getLocation(),
                hotel.getAddress(),
                hotel.getAmenities(),
                hotel.getImageUrl()
        );
    }
}
