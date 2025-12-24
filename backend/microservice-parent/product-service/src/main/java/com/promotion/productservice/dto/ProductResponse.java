package com.promotion.productservice.dto;

import com.promotion.productservice.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private String sellerName;
    private List<String> imageUrls;
    private Map<String, Object> attributes;


    public static ProductResponse fromEntity(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryId(product.getCategoryId())
                .sellerName(product.getSellerId())
                .imageUrls(product.getImageUrls())
                .attributes(product.getAttributes())
                .build();
    }

}


