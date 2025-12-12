package com.vittor.pennyapi.dto;

import com.vittor.pennyapi.enums.TransactionType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateTransactionDTO(
        @NotBlank(message = "Description is required")
        @Size(min = 1, max = 255, message = "Description must be between 1 and 255 characters")
        String description,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        @Digits(integer = 17, fraction = 2, message = "Amount must have at most 17 integer digits and 2 decimal places")
        BigDecimal amount,

        @NotNull(message = "Type is required")
        TransactionType type,

        @NotNull(message = "Date is required")
        @PastOrPresent(message = "Transaction date cannot be in the future")
        LocalDate date,

        @NotNull(message = "Category ID is required")
        UUID categoryId
) {
}
