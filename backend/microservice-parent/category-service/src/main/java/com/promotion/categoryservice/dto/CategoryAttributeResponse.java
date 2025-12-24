package com.promotion.categoryservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryAttributeResponse {
    private Long id;
    private String code;
    private String label;
    private String type;
    private boolean filterable;
    private boolean searchable;
}
