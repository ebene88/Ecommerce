package com.promotion.productservice.controller;

import com.promotion.productservice.dto.ApiResponse;
import com.promotion.productservice.dto.PaginatedResponse;
import com.promotion.productservice.dto.ProductRequest;
import com.promotion.productservice.dto.ProductResponse;
import com.promotion.productservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
@Tag(name = "Products", description = "Product management APIs")
public class ProductController {

    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new product",
            description = "Creates a product with optional images",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = ProductRequest.class)
                    )
            )
    )
    public ProductResponse createProduct(@RequestPart("product") ProductRequest productRequest,
                                         @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                         @AuthenticationPrincipal Jwt jwt){
        String sellerId = jwt.getSubject();
        return productService.create(productRequest, images, sellerId );
    }


    @GetMapping("/public")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get all products",
            description = "Returns paginated list of all products"
    )
    public ApiResponse<PaginatedResponse<ProductResponse>, Object> getAllProducts(Pageable pageable, @RequestParam(value = "search", required = false) String search) {
        PaginatedResponse<ProductResponse> data = productService.getAllProducts(pageable, search);
        return new ApiResponse<>(
                "success",
                "Products fetched successfully",
                data,
                null
        );
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
