package com.promotion.categoryservice.controller;

import com.promotion.categoryservice.dto.ApiResponse;
import com.promotion.categoryservice.dto.CategoryRequest;
import com.promotion.categoryservice.dto.CategoryResponse;
import com.promotion.categoryservice.exception.GlobalExceptionHandler;
import com.promotion.categoryservice.exception.NotFoundException;
import com.promotion.categoryservice.model.Category;
import com.promotion.categoryservice.repository.CategoryRepository;
import com.promotion.categoryservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryRepository categoryRepository;
    private  final CategoryService categoryService;

    @PostMapping
    public ApiResponse<CategoryResponse, Void> create(@RequestBody CategoryRequest request) {
        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(Long.valueOf(request.getParentId()))
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
        }
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .slug(request.getSlug())
                .parent(parent)
                .build();
        categoryRepository.save(category);


        return new ApiResponse<>(
                "success",
                "Category created successfully",
                mapToResponse(category, new ArrayList<>()),
                null
        );
    }

    @GetMapping
    public ApiResponse<List<CategoryResponse>, Void> getAll() {
        List<Category> categories = categoryRepository.findAll();

        Map<String, List<Category>> groupedByParent = categories.stream()
                .collect(Collectors.groupingBy(
                        cat -> cat.getParent() == null
                                ? "root"
                                : String.valueOf(cat.getParent().getId())
                ));

        List<CategoryResponse> tree = buildTree(groupedByParent, "root");

        return new ApiResponse<>(
                "success",
                "Categories fetched successfully",
                tree,
                null
        );
    }

    @GetMapping("/{id}")
    public CategoryResponse getById(@PathVariable Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        return mapToResponse(category, new ArrayList<>());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.ok("Category deleted successfully");
    }

    private List<CategoryResponse> buildTree(Map<String, List<Category>> grouped, String parentId) {
        return grouped.getOrDefault(parentId, Collections.emptyList()).stream()
                .map(cat -> mapToResponse(cat, buildTree(grouped, String.valueOf(cat.getId()))))
                .toList();
    }

    private CategoryResponse mapToResponse(Category category, List<CategoryResponse> children) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .slug(category.getSlug())
                .parentId(String.valueOf(category.getParent() != null ? category.getParent().getId() : null))
                .children(children)
                .build();
    }

}
