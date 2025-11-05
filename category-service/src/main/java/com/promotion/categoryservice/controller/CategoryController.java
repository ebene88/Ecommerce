package com.promotion.categoryservice.controller;

import com.promotion.categoryservice.dto.CategoryRequest;
import com.promotion.categoryservice.dto.CategoryResponse;
import com.promotion.categoryservice.model.Category;
import com.promotion.categoryservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
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

    @PostMapping
    public CategoryResponse create(@RequestBody CategoryRequest request) {
        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
        }
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .slug(request.getSlug())
                .parent(parent)
                .build();
        categoryRepository.save(category);
        return mapToResponse(category, new ArrayList<>());
    }

    @GetMapping
    public List<CategoryResponse> getAll() {
        List<Category> categories = categoryRepository.findAll();

        Map<String, List<Category>> groupedByParent = categories.stream()
                .collect(Collectors.groupingBy(cat -> cat.getParent() == null ? "root" : String.valueOf(cat.getParent().getId())));

        return buildTree(groupedByParent, "root");
    }

    @GetMapping("/{id}")
    public CategoryResponse getById(@PathVariable String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return mapToResponse(category, new ArrayList<>());
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
