package com.promotion.productservice.dto;

import lombok.Data;
import org.checkerframework.common.value.qual.MinLen;

@Data
public class PaginationRequestDTO {

    @MinLen(value = 0)
    private int page = 0;         // default

    @MinLen(value = 1)
    private int size = 10;       // default

    private String query;       // optional search text
}