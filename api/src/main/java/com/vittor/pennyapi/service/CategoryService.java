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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public CategoryResponseDTO create(CreateCategoryDTO dto, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Category category = new Category();
        category.setName(dto.name());
        category.setIcon(dto.icon());
        category.setColor(dto.color());
        category.setUser(user);

        Category savedCategory = categoryRepository.save(category);

        return new CategoryResponseDTO(savedCategory);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> findAll(UUID userId) {
        List<Category> categories = categoryRepository.findByUserId(userId);

        return categories.stream()
                .map(CategoryResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryResponseDTO findById(UUID id, UUID userId) {
        Category category = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found or does not belong to user"));

        return new CategoryResponseDTO(category);
    }

    @Transactional
    public CategoryResponseDTO update(UUID id, UpdateCategoryDTO dto, UUID userId) {
        Category category = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found or does not belong to user"));

        category.setName(dto.name());
        category.setIcon(dto.icon());
        category.setColor(dto.color());

        Category updatedCategory = categoryRepository.save(category);

        return new CategoryResponseDTO(updatedCategory);
    }

    @Transactional
    public void delete(UUID id, UUID userId) {
        if (!categoryRepository.existsByIdAndUserId(id, userId)) {
            throw new ResourceNotFoundException("Category not found or does not belong to user");
        }

        if (transactionRepository.existsByCategoryId(id)) {
            throw new BusinessRuleException("Cannot delete category with existing transactions");
        }

        categoryRepository.deleteByIdAndUserId(id, userId);
    }
}
