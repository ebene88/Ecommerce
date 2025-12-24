package com.promotion.productservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class ProductSearchDTO {
    private String id;
    private String name;
    private String description;
    private Long categoryId; // match your request field
    private BigDecimal price;
    private List<String> imageUrls; // match your request
    private Map<String, Object> attributes;

}
