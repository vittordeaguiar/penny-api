package com.vittor.pennyapi.dto;

import java.util.List;

public record ValidationErrorResponseDTO(
        String timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldErrorDTO> errors
) {
}
