package com.vittor.pennyapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCategoryDTO(
        @NotBlank(message = "Name is required")
        @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters")
        String name,

        @NotBlank(message = "Icon is required")
        @Size(max = 50, message = "Icon must not exceed 50 characters")
        String icon,

        @NotBlank(message = "Color is required")
        @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Color must be a valid hex format (#FFF or #FFFFFF)")
        String color
) {
}
