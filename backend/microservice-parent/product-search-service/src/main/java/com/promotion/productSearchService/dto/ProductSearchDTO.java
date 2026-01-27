package com.promotion.productSearchService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearchDTO {
    private String keyword;
    private int categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private int page = 0;
    private int size = 20;
    private String sortField = "price";
    private String sortDirection = "asc";
    private Map<String, Object> attributes;

}
