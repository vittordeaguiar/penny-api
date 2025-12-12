package com.vittor.pennyapi.controller;

import com.vittor.pennyapi.dto.CategoryResponseDTO;
import com.vittor.pennyapi.dto.CreateCategoryDTO;
import com.vittor.pennyapi.dto.UpdateCategoryDTO;
import com.vittor.pennyapi.entity.User;
import com.vittor.pennyapi.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(@RequestBody @Valid CreateCategoryDTO dto) {
        UUID userId = getCurrentUserId();
        CategoryResponseDTO response = categoryService.create(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> findAll() {
        UUID userId = getCurrentUserId();
        List<CategoryResponseDTO> categories = categoryService.findAll(userId);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> findById(@PathVariable UUID id) {
        UUID userId = getCurrentUserId();
        CategoryResponseDTO category = categoryService.findById(id, userId);
        return ResponseEntity.ok(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateCategoryDTO dto) {
        UUID userId = getCurrentUserId();
        CategoryResponseDTO response = categoryService.update(id, dto, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID userId = getCurrentUserId();
        categoryService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    private UUID getCurrentUserId() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }
}
