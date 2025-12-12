package com.vittor.pennyapi.service;

import com.vittor.pennyapi.dto.RegisterDTO;
import com.vittor.pennyapi.entity.User;
import com.vittor.pennyapi.exception.BusinessRuleException;
import com.vittor.pennyapi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(RegisterDTO registerDTO) {
        if (userRepository.findByEmail(registerDTO.email()).isPresent()) {
            throw new BusinessRuleException("Email already registered");
        }

        User newUser = new User();
        newUser.setName(registerDTO.name());
        newUser.setEmail(registerDTO.email());
        newUser.setPassword(passwordEncoder.encode(registerDTO.password()));


        return userRepository.save(newUser);
    }
}
