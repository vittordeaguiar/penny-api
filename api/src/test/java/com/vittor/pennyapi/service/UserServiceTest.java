package com.vittor.pennyapi.service;

import com.vittor.pennyapi.dto.RegisterDTO;
import com.vittor.pennyapi.entity.User;
import com.vittor.pennyapi.exception.BusinessRuleException;
import com.vittor.pennyapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterDTO registerDTO;
    private User user;

    @BeforeEach
    void setUp() {
        registerDTO = new RegisterDTO("Test User", "test@example.com", "password123");
        user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
    }

    @Test
    @DisplayName("Should successfully register a new user")
    void registerUser_Success() {
        // Given
        when(userRepository.findByEmail(registerDTO.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerDTO.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User registeredUser = userService.registerUser(registerDTO);

        // Then
        assertNotNull(registeredUser);
        assertEquals(registerDTO.email(), registeredUser.getEmail());
        assertEquals("encodedPassword", registeredUser.getPassword());
        verify(userRepository, times(1)).findByEmail(registerDTO.email());
        verify(passwordEncoder, times(1)).encode(registerDTO.password());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when email is already registered")
    void registerUser_EmailAlreadyRegistered_ThrowsException() {
        // Given
        when(userRepository.findByEmail(registerDTO.email())).thenReturn(Optional.of(user));

        // When & Then
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            userService.registerUser(registerDTO);
        });

        assertEquals("Email already registered", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(registerDTO.email());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
