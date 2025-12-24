package com.promotion.productSearchService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T, TError> {
    private String status;      // "success" or "error"
    private String message;     // human-readable message
    private T data;             // main payload
    private TError error;       // error details
}
