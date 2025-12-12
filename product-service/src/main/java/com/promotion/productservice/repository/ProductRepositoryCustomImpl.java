package com.promotion.productservice.repository;

import com.promotion.productservice.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<Product> searchByNameOrDescription(String keyword, Pageable pageable) {
        Query query = new Query();

        if (keyword != null && !keyword.isEmpty()) {
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("name").regex(keyword, "i"),
                    Criteria.where("description").regex(keyword, "i")
            ));
        }

        long total = mongoTemplate.count(query, Product.class);

        // Apply pagination
        query.with(pageable);

        List<Product> products = mongoTemplate.find(query, Product.class);
        return new PageImpl<>(products, pageable, total);
    }
}
