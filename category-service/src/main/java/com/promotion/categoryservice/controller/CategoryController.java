package com.promotion.categoryservice.controller;

import com.promotion.categoryservice.dto.CategoryRequest;
import com.promotion.categoryservice.dto.CategoryResponse;
import com.promotion.categoryservice.model.Category;
import com.promotion.categoryservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryRepository categoryRepository;

    @PostMapping
    public CategoryResponse create(@RequestBody CategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .slug(request.getSlug())
                .build();
        categoryRepository.save(category);
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription(), category.getSlug());
    }

    @GetMapping
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(cat -> new CategoryResponse(cat.getId(), cat.getName(), cat.getDescription(), cat.getSlug()))
                .toList();
    }

    @GetMapping("/{id}")
    public CategoryResponse getById(@PathVariable String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return  new CategoryResponse(category.getId(), category.getName(), category.getDescription(), category.getSlug());
    }
}
