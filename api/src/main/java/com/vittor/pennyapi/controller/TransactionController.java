package com.vittor.pennyapi.controller;

import com.vittor.pennyapi.dto.CreateTransactionDTO;
import com.vittor.pennyapi.dto.TransactionResponseDTO;
import com.vittor.pennyapi.dto.TransactionSummaryDTO;
import com.vittor.pennyapi.dto.UpdateTransactionDTO;
import com.vittor.pennyapi.entity.User;
import com.vittor.pennyapi.service.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "Operations related to financial transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> create(@RequestBody @Valid CreateTransactionDTO dto) {
        UUID userId = getCurrentUserId();
        TransactionResponseDTO response = transactionService.create(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<TransactionResponseDTO>> findAll(Pageable pageable) {
        UUID userId = getCurrentUserId();
        Page<TransactionResponseDTO> transactions = transactionService.findAll(userId, pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> findById(@PathVariable UUID id) {
        UUID userId = getCurrentUserId();
        TransactionResponseDTO transaction = transactionService.findById(id, userId);
        return ResponseEntity.ok(transaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateTransactionDTO dto) {
        UUID userId = getCurrentUserId();
        TransactionResponseDTO response = transactionService.update(id, dto, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID userId = getCurrentUserId();
        transactionService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<TransactionSummaryDTO> getFinancialSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        UUID userId = getCurrentUserId();
        TransactionSummaryDTO summary = transactionService.getFinancialSummary(userId, startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    private UUID getCurrentUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }
}
