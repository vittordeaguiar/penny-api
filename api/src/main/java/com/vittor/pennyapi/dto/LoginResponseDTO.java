package com.vittor.pennyapi.dto;

public record LoginResponseDTO(
        String token,
        String type,
        String email,
        String name
) {
    public LoginResponseDTO(String token, String email, String name) {
        this(token, "Bearer", email, name);
    }
}
