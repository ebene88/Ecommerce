package com.promotion.categoryservice.dto;

import lombok.Data;

@Data
public class CategoryAttributeRequest {
    private String code;
    private String label;
    private String type;        // string, number
    private boolean filterable;
    private boolean searchable;
}
