package com.promotion.productservice.service;

import com.promotion.productservice.client.*;

import com.promotion.productservice.dto.PaginatedResponse;
import com.promotion.productservice.dto.ProductRequest;
import com.promotion.productservice.dto.ProductResponse;
import com.promotion.productservice.dto.ProductSearchDTO;
import com.promotion.productservice.model.Product;
import com.promotion.productservice.repository.ProductRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryClient categoryClient;
    private final UserClient userClient;
    private final ProductSearchClient productSearchClient;


    private final MinioService minioService;

    public ProductResponse create(ProductRequest request, List<MultipartFile> images, String sellerId) {

        CategoryResponse category = categoryClient.getCategoryById(request.getCategoryId());

        List<CategoryAttributeResponse> categoryAttributes =
                categoryClient.getCategoryAttributes(request.getCategoryId()).getData();
        List<String> imageUrls = minioService.uploadMultipleFiles(images);
        // Validate attribute
        validateAttributes(request.getAttributes(), categoryAttributes);

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .categoryId(category.getId())
                .sellerId(sellerId)
                .imageUrls(imageUrls)
                .attributes(request.getAttributes())
                .build();


        productRepository.save(product);


        ProductSearchDTO searchDTO = new ProductSearchDTO();
        searchDTO.setId(product.getId());
        searchDTO.setName(product.getName());
        searchDTO.setDescription(product.getDescription());
        searchDTO.setCategoryId(category.getId());
        searchDTO.setAttributes(product.getAttributes());
        searchDTO.setPrice(product.getPrice());
        searchDTO.setImageUrls(imageUrls);

        try {
            productSearchClient.indexProduct(searchDTO);
        } catch (Exception e) {
            // log but don't fail main product creation
            System.err.println("Failed to index product in Elasticsearch: " + e.getMessage());
        }

        return mapToProductResponse(product);
    }


    public PaginatedResponse<ProductResponse> getAllProducts(Pageable pageable, String search) {
        Page<Product> products;
        if (search == null || search.isEmpty()) {
            products = productRepository.findAll(pageable);
        } else {
            products = productRepository.searchByNameOrDescription(search, pageable);
        }

        List<ProductResponse> productResponses = products.stream()
                .map(this::mapToProductResponse)
                .toList();

        return new PaginatedResponse<>(
                productResponses,
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages(),
                products.hasNext()

        );
    }

    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return ProductResponse.fromEntity(product);
    }


    public List<ProductResponse> getProductsBySeller(String sellerId) {
        List<Product> products = productRepository.findBySellerId(sellerId);
        return products.stream()
                .map(this::mapToProductResponse)
                .toList();
    }


    public ProductResponse updateProduct(String productId, ProductRequest request, String sellerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getSellerId().equals(sellerId)) {
            throw new RuntimeException("You are not authorized to update this product");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setAttributes(request.getAttributes());


        productRepository.save(product);
        return mapToProductResponse(product);
    }

    public void deleteProduct(String productId, String sellerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getSellerId().equals(sellerId)) {
            throw new RuntimeException("You are not authorized to delete this product");
        }

        productRepository.delete(product);
    }


    private ProductResponse mapToProductResponse(Product product) {

        UserResponse seller = null;

        if (product.getSellerId() != null && !product.getSellerId().isEmpty()) {
            try {
                seller = userClient.getUserById(product.getSellerId());
            } catch (FeignException.NotFound e) {
                // Handle case where user does not exist
                seller = new UserResponse(product.getSellerId(), "Unknown Seller");
            }
        } else {
            seller = new UserResponse(null, "Unknown Seller");
        }


        CategoryResponse category = null;

        if (product.getCategoryId() != null && !product.getCategoryId().describeConstable().isEmpty()) {
            try {
                category = categoryClient.getCategoryById(product.getCategoryId());
            } catch (FeignException.NotFound e) {
                // Handle case where category does not exist
                category = new CategoryResponse(product.getCategoryId(), "Unknown Category", "Unknown Category", "Unknown Category");
            }
        } else {
            category = new CategoryResponse(null, "Unknown Category","Unknown Category","Unknown Category");
        }
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryId(product.getCategoryId())
                .sellerName(seller.getUsername())
                .imageUrls(product.getImageUrls())
                .attributes(product.getAttributes())
                .build();
    }

    private void validateAttributes(
            Map<String, Object> attributes,
            List<CategoryAttributeResponse> categoryAttributes
    ) {
        if (attributes == null || attributes.isEmpty()) return;

        for (CategoryAttributeResponse attr : categoryAttributes) {
            Object value = attributes.get(attr.getCode());

            if (value == null) {
                continue; // optional attribute
            }

            switch (attr.getType()) {
                case "number" -> {
                    if (!(value instanceof Number)) {
                        throw new IllegalArgumentException(
                                "Attribute " + attr.getCode() + " must be a number"
                        );
                    }
                }
                case "boolean" -> {
                    if (!(value instanceof Boolean)) {
                        throw new IllegalArgumentException(
                                "Attribute " + attr.getCode() + " must be boolean"
                        );
                    }
                }
                case "string" -> {
                    if (!(value instanceof String)) {
                        throw new IllegalArgumentException(
                                "Attribute " + attr.getCode() + " must be string"
                        );
                    }
                }
            }
        }
    }

}
