package com.springboot.cozyheavenstay.mapper;

import com.springboot.cozyheavenstay.dto.request.AdminRequestDto;
import com.springboot.cozyheavenstay.dto.response.AdminUserResponseDto;
import com.springboot.cozyheavenstay.model.Admin;
import com.springboot.cozyheavenstay.model.User;
import org.springframework.stereotype.Component;

@Component
public class AdminMapper {

    public static Admin convertDtoToEntity(AdminRequestDto dto){
        Admin admin = new Admin();
        admin.setName(dto.name());
        admin.setEmail(dto.email());
        return admin;
    }

    public static AdminUserResponseDto convertUserToDto(User user) {
        return new AdminUserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.isActive()
        );
    }
}
