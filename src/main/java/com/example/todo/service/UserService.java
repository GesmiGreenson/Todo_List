package com.example.todo.service;

import com.example.todo.entity.User;
import com.example.todo.dto.UserRegistrationDto;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User save(UserRegistrationDto registrationDto);
    User findByEmail(String email);
}