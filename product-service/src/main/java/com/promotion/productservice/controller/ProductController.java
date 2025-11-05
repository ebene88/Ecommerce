package com.promotion.productservice.controller;

import com.promotion.productservice.dto.ProductRequest;
import com.promotion.productservice.dto.ProductResponse;
import com.promotion.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@RequestPart("product") ProductRequest productRequest,
                                         @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                         @AuthenticationPrincipal Jwt jwt){
        String sellerId = jwt.getSubject();
        return productService.create(productRequest, images, sellerId );
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/my-products")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getMyProducts(@AuthenticationPrincipal Jwt jwt) {
        String sellerId = jwt.getSubject(); // current user from token
        return productService.getProductsBySeller(sellerId);
    }

    @PutMapping("/{productId}")
    public ProductResponse updateProduct(
            @PathVariable String productId,
            @RequestBody ProductRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String sellerId = jwt.getSubject();
        return productService.updateProduct(productId, request, sellerId);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable String productId, @AuthenticationPrincipal Jwt jwt) {
        String sellerId = jwt.getSubject();
        productService.deleteProduct(productId, sellerId);
    }


}
