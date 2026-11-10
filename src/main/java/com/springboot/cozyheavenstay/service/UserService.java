package com.springboot.cozyheavenstay.service;

import com.springboot.cozyheavenstay.dto.response.UserDetailsDto;
import com.springboot.cozyheavenstay.exception.InactiveUserException;
import com.springboot.cozyheavenstay.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public UserDetailsDto getUserDetails(String loggedInUsername) {
        UserDetailsDto detailsDto = userRepository.getUserDetails(loggedInUsername);
        if(!detailsDto.isActive()) {
            log.warn("Inactive user attempted to access the application. Username: {}",
                    loggedInUsername);
            throw new InactiveUserException("User is disabled");
        }

        log.info("User details fetched successfully. Username: {}", loggedInUsername);
        return detailsDto;
    }
}
