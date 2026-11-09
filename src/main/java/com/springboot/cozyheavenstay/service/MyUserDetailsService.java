package com.springboot.cozyheavenstay.service;

import com.springboot.cozyheavenstay.exception.ResourceNotFoundException;
import com.springboot.cozyheavenstay.model.User;
import com.springboot.cozyheavenstay.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user= userRepository.loadUserByUsername(username,true);
        if(user==null)
            throw new ResourceNotFoundException("User not found or not active");

        return user;
    }
}
