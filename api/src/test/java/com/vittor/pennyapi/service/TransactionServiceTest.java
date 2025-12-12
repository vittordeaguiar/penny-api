package com.vittor.pennyapi.service;

import com.vittor.pennyapi.dto.CreateTransactionDTO;
import com.vittor.pennyapi.dto.TransactionResponseDTO;
import com.vittor.pennyapi.dto.TransactionSummaryDTO;
import com.vittor.pennyapi.dto.UpdateTransactionDTO;
import com.vittor.pennyapi.entity.Category;
import com.vittor.pennyapi.entity.Transaction;
import com.vittor.pennyapi.entity.User;
import com.vittor.pennyapi.enums.TransactionType;
import com.vittor.pennyapi.exception.BusinessRuleException;
import com.vittor.pennyapi.exception.ResourceNotFoundException;
import com.vittor.pennyapi.repository.CategoryRepository;
import com.vittor.pennyapi.repository.TransactionRepository;
import com.vittor.pennyapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TransactionService transactionService;

    private UUID userId;
    private UUID categoryId;
    private UUID transactionId;
    private User user;
    private Category category;
    private Transaction transaction;
    private CreateTransactionDTO createTransactionDTO;
    private UpdateTransactionDTO updateTransactionDTO;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        transactionId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password");

        category = new Category();
        category.setId(categoryId);
        category.setName("Food");
        category.setIcon("icon-food");
        category.setColor("#FF0000");
        category.setUser(user);

        transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setDescription("Lunch");
        transaction.setAmount(BigDecimal.valueOf(25.00));
        transaction.setType(TransactionType.EXPENSE);
        transaction.setDate(LocalDate.now());
        transaction.setCategory(category);
        transaction.setUser(user);

        createTransactionDTO = new CreateTransactionDTO(
                "Dinner", BigDecimal.valueOf(50.00), TransactionType.EXPENSE, LocalDate.now(), categoryId
        );
        updateTransactionDTO = new UpdateTransactionDTO(
                "Shopping", BigDecimal.valueOf(75.00), TransactionType.EXPENSE, LocalDate.now(), categoryId
        );
    }

    @Test
    @DisplayName("Should create a transaction successfully")
    void create_Success() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndUserId(categoryId, userId)).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // When
        TransactionResponseDTO result = transactionService.create(createTransactionDTO, userId);

        // Then
        assertNotNull(result);
        assertEquals(transaction.getDescription(), result.description());
        assertEquals(transaction.getAmount(), result.amount());
        verify(userRepository, times(1)).findById(userId);
        verify(categoryRepository, times(1)).findByIdAndUserId(categoryId, userId);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when creating transaction for non-existent user")
    void create_UserNotFound_ThrowsResourceNotFoundException() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.create(createTransactionDTO, userId);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(categoryRepository, never()).findByIdAndUserId(any(UUID.class), any(UUID.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when creating transaction with non-existent category for user")
    void create_CategoryNotFound_ThrowsResourceNotFoundException() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndUserId(categoryId, userId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.create(createTransactionDTO, userId);
        });

        assertEquals("Category not found or does not belong to user", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(categoryRepository, times(1)).findByIdAndUserId(categoryId, userId);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should return a page of transactions for a given user")
    void findAll_ReturnsPageOfTransactions() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction), pageable, 1);
        when(transactionRepository.findByUserId(userId, pageable)).thenReturn(transactionPage);

        // When
        Page<TransactionResponseDTO> result = transactionService.findAll(userId, pageable);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(transaction.getDescription(), result.getContent().get(0).description());
        verify(transactionRepository, times(1)).findByUserId(userId, pageable);
    }

    @Test
    @DisplayName("Should return an empty page when no transactions found for user")
    void findAll_ReturnsEmptyPage_WhenNoTransactions() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(transactionRepository.findByUserId(userId, pageable)).thenReturn(emptyPage);

        // When
        Page<TransactionResponseDTO> result = transactionService.findAll(userId, pageable);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(transactionRepository, times(1)).findByUserId(userId, pageable);
    }

    @Test
    @DisplayName("Should find transaction by ID for a given user")
    void findById_Success() {
        // Given
        when(transactionRepository.findByIdAndUserId(transactionId, userId)).thenReturn(Optional.of(transaction));

        // When
        TransactionResponseDTO result = transactionService.findById(transactionId, userId);

        // Then
        assertNotNull(result);
        assertEquals(transaction.getDescription(), result.description());
        verify(transactionRepository, times(1)).findByIdAndUserId(transactionId, userId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when transaction not found by ID for user")
    void findById_NotFound_ThrowsResourceNotFoundException() {
        // Given
        when(transactionRepository.findByIdAndUserId(transactionId, userId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.findById(transactionId, userId);
        });

        assertEquals("Transaction not found or does not belong to user", exception.getMessage());
        verify(transactionRepository, times(1)).findByIdAndUserId(transactionId, userId);
    }

    @Test
    @DisplayName("Should update transaction successfully")
    void update_Success() {
        // Given
        UUID newCategoryId = UUID.randomUUID();
        Category newCategory = new Category();
        newCategory.setId(newCategoryId);
        newCategory.setName("New Category");
        newCategory.setUser(user);

        UpdateTransactionDTO updatedDto = new UpdateTransactionDTO(
                "New Desc", BigDecimal.valueOf(100.00), TransactionType.INCOME, LocalDate.now().plusDays(1), newCategoryId
        );

        when(transactionRepository.findByIdAndUserId(transactionId, userId)).thenReturn(Optional.of(transaction));
        when(categoryRepository.findByIdAndUserId(newCategoryId, userId)).thenReturn(Optional.of(newCategory));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // When
        TransactionResponseDTO result = transactionService.update(transactionId, updatedDto, userId);

        // Then
        assertNotNull(result);
        assertEquals(updatedDto.description(), result.description());
        assertEquals(updatedDto.amount(), result.amount());
        assertEquals(updatedDto.type(), result.type());
        assertEquals(updatedDto.date(), result.date());
        verify(transactionRepository, times(1)).findByIdAndUserId(transactionId, userId);
        verify(categoryRepository, times(1)).findByIdAndUserId(newCategoryId, userId);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent transaction for user")
    void update_TransactionNotFound_ThrowsResourceNotFoundException() {
        // Given
        when(transactionRepository.findByIdAndUserId(transactionId, userId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.update(transactionId, updateTransactionDTO, userId);
        });

        assertEquals("Transaction not found or does not belong to user", exception.getMessage());
        verify(transactionRepository, times(1)).findByIdAndUserId(transactionId, userId);
        verify(categoryRepository, never()).findByIdAndUserId(any(UUID.class), any(UUID.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating transaction with non-existent new category for user")
    void update_CategoryNotFound_ThrowsResourceNotFoundException() {
        // Given
        when(transactionRepository.findByIdAndUserId(transactionId, userId)).thenReturn(Optional.of(transaction));
        when(categoryRepository.findByIdAndUserId(updateTransactionDTO.categoryId(), userId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.update(transactionId, updateTransactionDTO, userId);
        });

        assertEquals("Category not found or does not belong to user", exception.getMessage());
        verify(transactionRepository, times(1)).findByIdAndUserId(transactionId, userId);
        verify(categoryRepository, times(1)).findByIdAndUserId(updateTransactionDTO.categoryId(), userId);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should delete transaction successfully")
    void delete_Success() {
        // Given
        when(transactionRepository.existsByIdAndUserId(transactionId, userId)).thenReturn(true);
        doNothing().when(transactionRepository).deleteByIdAndUserId(transactionId, userId);

        // When
        transactionService.delete(transactionId, userId);

        // Then
        verify(transactionRepository, times(1)).existsByIdAndUserId(transactionId, userId);
        verify(transactionRepository, times(1)).deleteByIdAndUserId(transactionId, userId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent transaction for user")
    void delete_NotFound_ThrowsResourceNotFoundException() {
        // Given
        when(transactionRepository.existsByIdAndUserId(transactionId, userId)).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.delete(transactionId, userId);
        });

        assertEquals("Transaction not found or does not belong to user", exception.getMessage());
        verify(transactionRepository, times(1)).existsByIdAndUserId(transactionId, userId);
        verify(transactionRepository, never()).deleteByIdAndUserId(any(UUID.class), any(UUID.class));
    }

    @Test
    @DisplayName("Should return financial summary for current month if no dates provided")
    void getFinancialSummary_DefaultDates_ReturnsSummary() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        List<Object[]> summaryResult = Collections.singletonList(new Object[]{BigDecimal.valueOf(1000.00), BigDecimal.valueOf(500.00)});
        when(transactionRepository.calculateFinancialSummary(any(UUID.class), any(LocalDate.class), any(LocalDate.class))).thenReturn(summaryResult);

        // When
        TransactionSummaryDTO result = transactionService.getFinancialSummary(userId, null, null);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(1000.00), result.totalIncome());
        assertEquals(BigDecimal.valueOf(500.00), result.totalExpense());
        assertEquals(BigDecimal.valueOf(500.00), result.balance()); // 1000 - 500
        verify(userRepository, times(1)).findById(userId);
        verify(transactionRepository, times(1)).calculateFinancialSummary(eq(userId), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("Should return financial summary for custom date range")
    void getFinancialSummary_CustomDates_ReturnsSummary() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        List<Object[]> summaryResult = Collections.singletonList(new Object[]{BigDecimal.valueOf(2000.00), BigDecimal.valueOf(1200.00)});
        when(transactionRepository.calculateFinancialSummary(userId, startDate, endDate)).thenReturn(summaryResult);

        // When
        TransactionSummaryDTO result = transactionService.getFinancialSummary(userId, startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(2000.00), result.totalIncome());
        assertEquals(BigDecimal.valueOf(1200.00), result.totalExpense());
        assertEquals(BigDecimal.valueOf(800.00), result.balance()); // 2000 - 1200
        verify(userRepository, times(1)).findById(userId);
        verify(transactionRepository, times(1)).calculateFinancialSummary(userId, startDate, endDate);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when getting summary for non-existent user")
    void getFinancialSummary_UserNotFound_ThrowsResourceNotFoundException() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.getFinancialSummary(userId, null, null);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(transactionRepository, never()).calculateFinancialSummary(any(UUID.class), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when startDate is after endDate")
    void getFinancialSummary_InvalidDateRange_ThrowsBusinessRuleException() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 1, 31);
        LocalDate endDate = LocalDate.of(2025, 1, 1);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When & Then
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            transactionService.getFinancialSummary(userId, startDate, endDate);
        });

        assertEquals("Start date cannot be after end date", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(transactionRepository, never()).calculateFinancialSummary(any(UUID.class), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("Should return zero summary if calculateFinancialSummary returns empty result")
    void getFinancialSummary_EmptyCalculationResult_ReturnsZeroSummary() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(transactionRepository.calculateFinancialSummary(any(UUID.class), any(LocalDate.class), any(LocalDate.class))).thenReturn(Collections.emptyList());

        // When
        TransactionSummaryDTO result = transactionService.getFinancialSummary(userId, null, null);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.totalIncome());
        assertEquals(BigDecimal.ZERO, result.totalExpense());
        assertEquals(BigDecimal.ZERO, result.balance());
        verify(userRepository, times(1)).findById(userId);
        verify(transactionRepository, times(1)).calculateFinancialSummary(eq(userId), any(LocalDate.class), any(LocalDate.class));
    }
}
