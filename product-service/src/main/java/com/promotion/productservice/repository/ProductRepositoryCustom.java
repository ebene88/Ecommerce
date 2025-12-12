package com.promotion.productservice.repository;

import com.promotion.productservice.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<Product> searchByNameOrDescription(String keyword, Pageable pageable);
}
