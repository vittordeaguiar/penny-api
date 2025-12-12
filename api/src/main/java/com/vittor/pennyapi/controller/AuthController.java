package com.vittor.pennyapi.controller;

import com.vittor.pennyapi.dto.LoginDTO;
import com.vittor.pennyapi.dto.LoginResponseDTO;
import com.vittor.pennyapi.dto.RegisterDTO;
import com.vittor.pennyapi.entity.User;
import com.vittor.pennyapi.exception.BusinessRuleException;
import com.vittor.pennyapi.repository.UserRepository;
import com.vittor.pennyapi.security.TokenService;
import com.vittor.pennyapi.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Operations related to user authentication")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService; // Inject the new UserService

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO registerDTO) {
        userService.registerUser(registerDTO); // Delegate to UserService
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO loginDTO) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password());

        Authentication auth = authenticationManager.authenticate(usernamePassword);
        User user = (User) auth.getPrincipal();

        String token = tokenService.generateToken(user);

        LoginResponseDTO response = new LoginResponseDTO(token, user.getEmail(), user.getName());

        return ResponseEntity.ok(response);
    }
}
