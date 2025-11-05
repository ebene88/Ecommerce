package com.promotion.productservice.client;

import com.promotion.productservice.dto.ProductSearchDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-search-service") // must match your search service name in Eureka
public interface ProductSearchClient {

    @PostMapping("/api/products/index")
    void indexProduct(@RequestBody ProductSearchDTO product);
}
