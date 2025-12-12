package com.vittor.pennyapi.integration;

import com.vittor.pennyapi.dto.LoginDTO;
import com.vittor.pennyapi.dto.RegisterDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Authentication Integration Tests")
class AuthenticationIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Should reject duplicate email registration")
    void shouldRejectDuplicateEmail() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO(
                "Jane Doe",
                "jane@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(registerDTO)))
                .andExpect(status().isCreated());

        // Tentativa de registro duplicado
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(registerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already registered"));
    }

    @Test
    @DisplayName("Should reject login with invalid credentials")
    void shouldRejectInvalidCredentials() throws Exception {
        LoginDTO loginDTO = new LoginDTO(
                "nonexistent@example.com",
                "wrongpassword"
        );

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(loginDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should reject access to protected endpoints without token")
    void shouldRejectUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should reject invalid JWT token")
    void shouldRejectInvalidToken() throws Exception {
        String invalidToken = "invalid.jwt.token";

        mockMvc.perform(get("/api/categories")
                .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isForbidden());
    }
}
