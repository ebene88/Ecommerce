package com.promotion.productservice.service;

import com.promotion.productservice.client.CategoryClient;
import com.promotion.productservice.client.CategoryResponse;
import com.promotion.productservice.dto.ProductRequest;
import com.promotion.productservice.dto.ProductResponse;
import com.promotion.productservice.model.Product;
import com.promotion.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryClient categoryClient;

    public ProductResponse create(ProductRequest request) {
        // verify category exists
        CategoryResponse category = categoryClient.getCategoryById(request.getCategoryId());

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .categoryId(category.getId())
                .build();

        productRepository.save(product);

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryName(category.getName())
                .build();
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return  products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryName(product.getCategoryId())
                .build();
    }
}
