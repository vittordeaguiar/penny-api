package com.vittor.pennyapi.service;

import com.vittor.pennyapi.dto.CategoryResponseDTO;
import com.vittor.pennyapi.dto.CreateCategoryDTO;
import com.vittor.pennyapi.dto.UpdateCategoryDTO;
import com.vittor.pennyapi.entity.Category;
import com.vittor.pennyapi.entity.User;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CategoryService categoryService;

    private UUID userId;
    private UUID categoryId;
    private User user;
    private Category category;
    private CreateCategoryDTO createCategoryDTO;
    private UpdateCategoryDTO updateCategoryDTO;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

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

        createCategoryDTO = new CreateCategoryDTO("Shopping", "icon-shop", "#00FF00");
        updateCategoryDTO = new UpdateCategoryDTO("Travel", "icon-plane", "#0000FF");
    }

    @Test
    @DisplayName("Should create a category successfully")
    void create_Success() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // When
        CategoryResponseDTO result = categoryService.create(createCategoryDTO, userId);

        // Then
        assertNotNull(result);
        assertEquals(category.getName(), result.name());
        assertEquals(category.getIcon(), result.icon());
        assertEquals(category.getColor(), result.color());
        verify(userRepository, times(1)).findById(userId);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when creating category for non-existent user")
    void create_UserNotFound_ThrowsResourceNotFoundException() {
        // Given
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.create(createCategoryDTO, userId);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Should find all categories for a given user")
    void findAll_ReturnsListOfCategories() {
        // Given
        when(categoryRepository.findByUserId(userId)).thenReturn(List.of(category));

        // When
        List<CategoryResponseDTO> result = categoryService.findAll(userId);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(category.getName(), result.get(0).name());
        verify(categoryRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Should return empty list when no categories found for user")
    void findAll_ReturnsEmptyList_WhenNoCategories() {
        // Given
        when(categoryRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        // When
        List<CategoryResponseDTO> result = categoryService.findAll(userId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(categoryRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Should find category by ID for a given user")
    void findById_Success() {
        // Given
        when(categoryRepository.findByIdAndUserId(categoryId, userId)).thenReturn(Optional.of(category));

        // When
        CategoryResponseDTO result = categoryService.findById(categoryId, userId);

        // Then
        assertNotNull(result);
        assertEquals(category.getName(), result.name());
        verify(categoryRepository, times(1)).findByIdAndUserId(categoryId, userId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when category not found by ID for user")
    void findById_NotFound_ThrowsResourceNotFoundException() {
        // Given
        when(categoryRepository.findByIdAndUserId(categoryId, userId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.findById(categoryId, userId);
        });

        assertEquals("Category not found or does not belong to user", exception.getMessage());
        verify(categoryRepository, times(1)).findByIdAndUserId(categoryId, userId);
    }

    @Test
    @DisplayName("Should update category successfully")
    void update_Success() {
        // Given
        when(categoryRepository.findByIdAndUserId(categoryId, userId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // When
        CategoryResponseDTO result = categoryService.update(categoryId, updateCategoryDTO, userId);

        // Then
        assertNotNull(result);
        assertEquals(updateCategoryDTO.name(), result.name());
        assertEquals(updateCategoryDTO.icon(), result.icon());
        assertEquals(updateCategoryDTO.color(), result.color());
        verify(categoryRepository, times(1)).findByIdAndUserId(categoryId, userId);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent category for user")
    void update_NotFound_ThrowsResourceNotFoundException() {
        // Given
        when(categoryRepository.findByIdAndUserId(categoryId, userId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.update(categoryId, updateCategoryDTO, userId);
        });

        assertEquals("Category not found or does not belong to user", exception.getMessage());
        verify(categoryRepository, times(1)).findByIdAndUserId(categoryId, userId);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Should delete category successfully")
    void delete_Success() {
        // Given
        when(categoryRepository.existsByIdAndUserId(categoryId, userId)).thenReturn(true);
        when(transactionRepository.existsByCategoryId(categoryId)).thenReturn(false);
        doNothing().when(categoryRepository).deleteByIdAndUserId(categoryId, userId);

        // When
        categoryService.delete(categoryId, userId);

        // Then
        verify(categoryRepository, times(1)).existsByIdAndUserId(categoryId, userId);
        verify(transactionRepository, times(1)).existsByCategoryId(categoryId);
        verify(categoryRepository, times(1)).deleteByIdAndUserId(categoryId, userId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent category for user")
    void delete_NotFound_ThrowsResourceNotFoundException() {
        // Given
        when(categoryRepository.existsByIdAndUserId(categoryId, userId)).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.delete(categoryId, userId);
        });

        assertEquals("Category not found or does not belong to user", exception.getMessage());
        verify(categoryRepository, times(1)).existsByIdAndUserId(categoryId, userId);
        verify(transactionRepository, never()).existsByCategoryId(any(UUID.class));
        verify(categoryRepository, never()).deleteByIdAndUserId(any(UUID.class), any(UUID.class));
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when deleting category with existing transactions")
    void delete_CategoryHasTransactions_ThrowsBusinessRuleException() {
        // Given
        when(categoryRepository.existsByIdAndUserId(categoryId, userId)).thenReturn(true);
        when(transactionRepository.existsByCategoryId(categoryId)).thenReturn(true);

        // When & Then
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            categoryService.delete(categoryId, userId);
        });

        assertEquals("Cannot delete category with existing transactions", exception.getMessage());
        verify(categoryRepository, times(1)).existsByIdAndUserId(categoryId, userId);
        verify(transactionRepository, times(1)).existsByCategoryId(categoryId);
        verify(categoryRepository, never()).deleteByIdAndUserId(any(UUID.class), any(UUID.class));
    }
}
