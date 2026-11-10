package com.springboot.cozyheavenstay.controller;

import com.springboot.cozyheavenstay.dto.response.TokenDto;
import com.springboot.cozyheavenstay.dto.response.UserDetailsDto;
import com.springboot.cozyheavenstay.service.UserService;
import com.springboot.cozyheavenstay.utility.JwtUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173/")
public class AuthController {

    private final JwtUtility jwtUtility;
    private final UserService userService;

    @GetMapping("/api/auth/login")
    public TokenDto login(Principal principal){
        log.info("Generating authentication token for user: {}", principal.getName());

        String token = jwtUtility.generateToken(principal.getName());
        log.info("Authentication token generated successfully for user: {}", principal.getName());
        return new TokenDto(
            token,
                jwtUtility.extractExpiration(token),
                LocalDateTime.now().toString()
        );
    }

    @GetMapping("/api/auth/user-details")
    public UserDetailsDto getUserDetails(Principal principal){
        log.info("Fetching user details for: {}", principal.getName());
        String loggedInUsername = principal.getName();
        return userService.getUserDetails(loggedInUsername);
    }
}
