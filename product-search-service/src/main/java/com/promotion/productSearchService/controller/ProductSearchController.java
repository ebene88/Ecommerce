package com.promotion.productSearchService.controller;

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




    @PostMapping("/search")
    public List<ProductDocument> search(@RequestBody ProductSearchDTO request) throws IOException {
        return productSearchService.searchProducts(
                request.getKeyword(),
                request.getCategory(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getPage(),
                request.getSize(),
                request.getSortField(),
                request.getSortDirection()
        );
    }

    @GetMapping("/filter")
    public List<ProductDocument> filterProducts(
            @RequestBody ProductSearchDTO request
    ) throws IOException {
        return productSearchService.searchProducts(request.getKeyword(),
                request.getCategory(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getPage(),
                request.getSize(),
                request.getSortField(),
                request.getSortDirection());
    }


    // 🔹 Delete a product by ID
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) throws IOException {
        productSearchService.delete(id);
    }
}
