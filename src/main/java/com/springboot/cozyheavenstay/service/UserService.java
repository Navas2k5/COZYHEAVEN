package com.springboot.cozyheavenstay.service;

import com.springboot.cozyheavenstay.dto.response.UserDetailsDto;
import com.springboot.cozyheavenstay.exception.InactiveUserException;
import com.springboot.cozyheavenstay.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public UserDetailsDto getUserDetails(String loggedInUsername) {
        UserDetailsDto detailsDto = userRepository.getUserDetails(loggedInUsername);
        if(!detailsDto.isActive())
            throw new InactiveUserException("User is disabled");
        return detailsDto;
    }
}
