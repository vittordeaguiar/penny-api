package com.vittor.pennyapi.dto;

import com.vittor.pennyapi.entity.Transaction;
import com.vittor.pennyapi.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponseDTO(
        UUID id,
        String description,
        BigDecimal amount,
        TransactionType type,
        LocalDate date,
        CategoryResponseDTO category,
        UUID userId,
        LocalDateTime createdAt
) {
    public TransactionResponseDTO(Transaction transaction) {
        this(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getDate(),
                new CategoryResponseDTO(transaction.getCategory()),
                transaction.getUser().getId(),
                transaction.getCreatedAt()
        );
    }
}
