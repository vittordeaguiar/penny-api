package com.vittor.pennyapi.dto;

public record ErrorResponseDTO(
        String timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
