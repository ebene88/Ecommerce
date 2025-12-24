package com.promotion.categoryservice.controller;

import com.promotion.categoryservice.dto.ApiResponse;
import com.promotion.categoryservice.dto.CategoryAttributeRequest;
import com.promotion.categoryservice.dto.CategoryAttributeResponse;
import com.promotion.categoryservice.service.CategoryAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories/{categoryId}/attributes")
@RequiredArgsConstructor
public class CategoryAttributeController {

    private final CategoryAttributeService attributeService;

    @PostMapping
    public ApiResponse<CategoryAttributeResponse, Void> create(
            @PathVariable Long categoryId,
            @RequestBody CategoryAttributeRequest request
    ) {
        return new ApiResponse<>(
                "success",
                "Attribute created successfully",
                attributeService.create(categoryId, request),
                null
        );
    }

    @GetMapping
    public ApiResponse<List<CategoryAttributeResponse>, Void> getByCategory(
            @PathVariable Long categoryId
    ) {
        return new ApiResponse<>(
                "success",
                "Attributes fetched successfully",
                attributeService.getByCategory(categoryId),
                null
        );
    }

    @PutMapping("/{attributeId}")
    public ApiResponse<CategoryAttributeResponse, Void> update(
            @PathVariable Long categoryId,
            @PathVariable Long attributeId,
            @RequestBody CategoryAttributeRequest request
    ) {
        return new ApiResponse<>(
                "success",
                "Attribute updated successfully",
                attributeService.update(categoryId, attributeId, request),
                null
        );
    }

    @DeleteMapping("/{attributeId}")
    public ApiResponse<Void, Void> delete(
            @PathVariable Long categoryId,
            @PathVariable Long attributeId
    ) {
        attributeService.delete(categoryId, attributeId);

        return new ApiResponse<>(
                "success",
                "Attribute deleted successfully",
                null,
                null
        );
    }

}
