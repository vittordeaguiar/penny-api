package com.vittor.pennyapi.dto;

public record FieldErrorDTO(
        String field,
        String message,
        Object rejectedValue
) {
}
