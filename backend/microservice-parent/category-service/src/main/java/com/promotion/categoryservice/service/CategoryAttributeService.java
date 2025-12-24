package com.promotion.categoryservice.service;

import com.promotion.categoryservice.dto.CategoryAttributeRequest;
import com.promotion.categoryservice.dto.CategoryAttributeResponse;
import com.promotion.categoryservice.model.Category;
import com.promotion.categoryservice.model.CategoryAttribute;
import com.promotion.categoryservice.repository.CategoryAttributeRepository;
import com.promotion.categoryservice.repository.CategoryRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryAttributeService {

    private final CategoryRepository categoryRepository;
    private final CategoryAttributeRepository attributeRepository;

    public CategoryAttributeResponse create(Long categoryId, CategoryAttributeRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        CategoryAttribute attribute = CategoryAttribute.builder()
                .category(category)
                .code(request.getCode())
                .label(request.getLabel())
                .type(request.getType())
                .filterable(request.isFilterable())
                .searchable(request.isSearchable())
                .build();

        attributeRepository.save(attribute);

        return map(attribute);
    }

    public List<CategoryAttributeResponse> getByCategory(Long categoryId) {
        return attributeRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::map)
                .toList();
    }

    private CategoryAttributeResponse map(CategoryAttribute attribute) {
        return CategoryAttributeResponse.builder()
                .id(attribute.getId())
                .code(attribute.getCode())
                .label(attribute.getLabel())
                .type(attribute.getType())
                .filterable(attribute.isFilterable())
                .searchable(attribute.isSearchable())
                .build();
    }


    public CategoryAttributeResponse update(
            Long categoryId,
            Long attributeId,
            CategoryAttributeRequest request
    ) {
        CategoryAttribute attribute = attributeRepository.findById(attributeId)
                .orElseThrow(() -> new NotFoundException("Category attribute not found"));

        // Safety check: ensure attribute belongs to category
        if (!attribute.getCategory().getId().equals(categoryId)) {
            throw new IllegalArgumentException("Attribute does not belong to this category");
        }

        attribute.setCode(request.getCode());
        attribute.setLabel(request.getLabel());
        attribute.setType(request.getType());
        attribute.setFilterable(request.isFilterable());
        attribute.setSearchable(request.isSearchable());

        attributeRepository.save(attribute);

        return map(attribute);
    }

    public void delete(Long categoryId, Long attributeId) {
        CategoryAttribute attribute = attributeRepository.findById(attributeId)
                .orElseThrow(() -> new NotFoundException("Category attribute not found"));

        if (!attribute.getCategory().getId().equals(categoryId)) {
            throw new IllegalArgumentException("Attribute does not belong to this category");
        }

        attributeRepository.delete(attribute);
    }

}
