package com.promotion.productservice.client;

import com.promotion.productservice.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "category-service")
public interface CategoryClient {
    @GetMapping("/api/categories/{id}")
    CategoryResponse getCategoryById(@PathVariable Long id);

    @GetMapping("/api/categories/{categoryId}/attributes")
    ApiResponse<List<CategoryAttributeResponse>, Void> getCategoryAttributes(
            @PathVariable("categoryId") Long categoryId
    );
}
