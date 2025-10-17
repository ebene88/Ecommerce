package com.promotion.productservice.repository;

import com.promotion.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ProductRepository  extends MongoRepository<Product, String> {
}
