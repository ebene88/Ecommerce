package com.promotion.categoryservice.service;

import com.promotion.categoryservice.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;



    public void deleteById(String id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found with id " + id);
        }
        categoryRepository.deleteById(id);
    }
}
