package com.vittor.pennyapi.dto;

import com.vittor.pennyapi.entity.Category;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoryResponseDTO(
        UUID id,
        String name,
        String icon,
        String color,
        LocalDateTime createdAt
) {
    public CategoryResponseDTO(Category category) {
        this(
                category.getId(),
                category.getName(),
                category.getIcon(),
                category.getColor(),
                category.getCreatedAt()
        );
    }
}
