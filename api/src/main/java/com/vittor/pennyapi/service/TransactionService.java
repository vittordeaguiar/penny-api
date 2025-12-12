package com.vittor.pennyapi.service;

import com.vittor.pennyapi.dto.CreateTransactionDTO;
import com.vittor.pennyapi.dto.TransactionResponseDTO;
import com.vittor.pennyapi.dto.TransactionSummaryDTO;
import com.vittor.pennyapi.dto.UpdateTransactionDTO;
import com.vittor.pennyapi.entity.Category;
import com.vittor.pennyapi.entity.Transaction;
import com.vittor.pennyapi.entity.User;
import com.vittor.pennyapi.exception.BusinessRuleException;
import com.vittor.pennyapi.exception.ResourceNotFoundException;
import com.vittor.pennyapi.repository.CategoryRepository;
import com.vittor.pennyapi.repository.TransactionRepository;
import com.vittor.pennyapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public TransactionResponseDTO create(CreateTransactionDTO dto, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Category category = categoryRepository.findByIdAndUserId(dto.categoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found or does not belong to user"));

        Transaction transaction = new Transaction();
        transaction.setDescription(dto.description());
        transaction.setAmount(dto.amount());
        transaction.setType(dto.type());
        transaction.setDate(dto.date());
        transaction.setCategory(category);
        transaction.setUser(user);

        Transaction savedTransaction = transactionRepository.save(transaction);

        return new TransactionResponseDTO(savedTransaction);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponseDTO> findAll(UUID userId, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByUserId(userId, pageable);

        return transactions.map(TransactionResponseDTO::new);
    }

    @Transactional(readOnly = true)
    public TransactionResponseDTO findById(UUID id, UUID userId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found or does not belong to user"));

        return new TransactionResponseDTO(transaction);
    }

    @Transactional
    public TransactionResponseDTO update(UUID id, UpdateTransactionDTO dto, UUID userId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found or does not belong to user"));

        Category category = categoryRepository.findByIdAndUserId(dto.categoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found or does not belong to user"));

        transaction.setDescription(dto.description());
        transaction.setAmount(dto.amount());
        transaction.setType(dto.type());
        transaction.setDate(dto.date());
        transaction.setCategory(category);

        Transaction updatedTransaction = transactionRepository.save(transaction);

        return new TransactionResponseDTO(updatedTransaction);
    }

    @Transactional
    public void delete(UUID id, UUID userId) {
        if (!transactionRepository.existsByIdAndUserId(id, userId)) {
            throw new ResourceNotFoundException("Transaction not found or does not belong to user");
        }

        transactionRepository.deleteByIdAndUserId(id, userId);
    }

    @Transactional(readOnly = true)
    public TransactionSummaryDTO getFinancialSummary(UUID userId, LocalDate startDate, LocalDate endDate) {
        // Valida que usuário existe (padrão existente)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Determina range de datas (default = mês atual)
        LocalDate effectiveStartDate;
        LocalDate effectiveEndDate;

        if (startDate == null && endDate == null) {
            // Default: mês atual
            LocalDate today = LocalDate.now();
            effectiveStartDate = today.withDayOfMonth(1);
            effectiveEndDate = today.withDayOfMonth(today.lengthOfMonth());
        } else if (startDate == null) {
            // Apenas endDate fornecido: usa início daquele mês
            effectiveStartDate = endDate.withDayOfMonth(1);
            effectiveEndDate = endDate;
        } else if (endDate == null) {
            // Apenas startDate fornecido: usa fim daquele mês
            effectiveStartDate = startDate;
            effectiveEndDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        } else {
            // Ambos fornecidos: valida
            if (startDate.isAfter(endDate)) {
                throw new BusinessRuleException("Start date cannot be after end date");
            }
            effectiveStartDate = startDate;
            effectiveEndDate = endDate;
        }

        // Executa query
        java.util.List<Object[]> resultList = transactionRepository.calculateFinancialSummary(
                userId,
                effectiveStartDate,
                effectiveEndDate
        );

        if (resultList.isEmpty()) {
             // Should not happen with aggregate query unless something is very wrong, 
             // but handle gracefully
             return new TransactionSummaryDTO(BigDecimal.ZERO, BigDecimal.ZERO, effectiveStartDate, effectiveEndDate);
        }

        Object[] result = resultList.get(0);

        // Extrai e converte resultados
        BigDecimal totalIncome = (BigDecimal) result[0];
        BigDecimal totalExpense = (BigDecimal) result[1];

        // Retorna DTO (balance calculado no constructor)
        return new TransactionSummaryDTO(totalIncome, totalExpense, effectiveStartDate, effectiveEndDate);
    }
}
