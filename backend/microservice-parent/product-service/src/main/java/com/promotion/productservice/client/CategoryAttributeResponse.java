package com.promotion.productservice.client;

import lombok.Data;

@Data
public class CategoryAttributeResponse {
    private String code;
    private String type; // string, number, boolean
    private boolean required;
}