package com.promotion.productSearchService.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSearchDTO {
    private String keyword;
    private String category;
    private String brand;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private int page = 0;
    private int size = 20;
    private String sortField = "price";
    private String sortDirection = "asc";
}
