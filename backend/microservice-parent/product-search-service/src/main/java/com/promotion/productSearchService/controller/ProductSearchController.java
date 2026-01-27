package com.promotion.productSearchService.controller;

import com.promotion.productSearchService.dto.ApiResponse;
import com.promotion.productSearchService.dto.PaginatedResponse;
import com.promotion.productSearchService.dto.ProductSearchDTO;
import com.promotion.productSearchService.model.ProductDocument;
import com.promotion.productSearchService.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductSearchService productSearchService;



    @PostMapping("/index")
    public void indexProduct(@RequestBody ProductDocument product) throws IOException {
        productSearchService.save(product);
    }


    @GetMapping("/search")
    public ApiResponse<PaginatedResponse<ProductDocument>, Void> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) throws IOException {

        PaginatedResponse<ProductDocument> paginated = productSearchService.searchByKeyword(keyword, page, size);

        return new ApiResponse<>(
                "success",
                "Products fetched successfully",
                paginated,
                null
        );
    }


    // 🔹 Filter search (category, price, attributes, sorting)
    @PostMapping("/filter")
    public ApiResponse<PaginatedResponse<ProductDocument>, Void> filterProducts(
            @RequestBody ProductSearchDTO request
    ) throws IOException {
        PaginatedResponse<ProductDocument> paginated =
                productSearchService.searchProducts(request);

        return new ApiResponse<>(
                "success",
                "Filtered products fetched successfully",
                paginated,
                null
        );
    }

    @GetMapping("/suggest")
    public ApiResponse<List<String>, Void> suggest(
            @RequestParam String q
    ) throws IOException {

        List<String> suggestions = productSearchService.suggestByPrefix(q);

        return new ApiResponse<>(
                "success",
                "Suggestions fetched successfully",
                suggestions,
                null
        );
    }


    // 🔹 Delete a product by ID
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) throws IOException {
        productSearchService.delete(id);
    }
}
