package com.promotion.productservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductSearchDTO {
    private String id;
    private String name;
    private String description;
    private String categoryId; // match your request field
    private BigDecimal price;
    private List<String> imageUrls; // match your request
}
