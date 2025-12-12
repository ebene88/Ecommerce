package com.promotion.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PaginatedResponse<T> {
    private List<T> content;       // actual data
    private int page;              // current page number
    private int size;              // page size
    private long totalElements;    // total number of items
    private int totalPages;        // total pages
}
