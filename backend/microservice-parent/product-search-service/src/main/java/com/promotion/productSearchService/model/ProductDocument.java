package com.promotion.productSearchService.model;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;


@Data
public class ProductDocument {
    private String id;
    private String name;
    private String description;
    private String categoryId; // match your request field
    private BigDecimal price;
    private List<String> imageUrls; // match your request
}

