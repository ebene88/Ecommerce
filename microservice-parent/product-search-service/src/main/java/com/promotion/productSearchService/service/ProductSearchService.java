package com.promotion.productSearchService.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import com.promotion.productSearchService.model.ProductDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSearchService {


    private final ElasticsearchClient elasticsearchClient; // Use ElasticsearchClient



    public void save(ProductDocument dto) throws IOException {
        elasticsearchClient.index(i -> i
                .index("products")
                .id(dto.getId())
                .document(dto)
        );
    }

    // 🔹 Delete a product by ID
    public void delete(String id) throws IOException {
        elasticsearchClient.delete(d -> d
                .index("products")
                .id(id)
        );
    }


    // 🔹 Multi-filter search with pagination and sorting
    public List<ProductDocument> searchProducts(
            String keyword,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size,
            String sortField,
            String sortDirection
    ) throws IOException {

        // Build queries
        Query keywordQuery;
        if (keyword != null && !keyword.isEmpty()) {
            keywordQuery = QueryBuilders.multiMatch(m -> m
                    .fields("name", "description")
                    .query(keyword)
            );
        } else {
            keywordQuery = null;
        }

        Query categoryQuery;
        if (category != null && !category.isEmpty()) {
            categoryQuery = QueryBuilders.term(t -> t
                    .field("category")
                    .value(category)
            );
        } else {
            categoryQuery = null;
        }

        Query priceQuery;
        if (minPrice != null && maxPrice != null) {
            priceQuery = QueryBuilders.range(r -> r
                    .field("price")
                    .gte(JsonData.of(minPrice))
                    .lte(JsonData.of(maxPrice))
            );
        } else if (minPrice != null) {
            priceQuery = QueryBuilders.range(r -> r
                    .field("price")
                    .gte(JsonData.of(minPrice))
            );
        } else if (maxPrice != null) {
            priceQuery = QueryBuilders.range(r -> r
                    .field("price")
                    .lte(JsonData.of(maxPrice))
            );
        } else {
            priceQuery = null;
        }

        // Combine filters using bool query
        Query boolQuery = QueryBuilders.bool(b -> {
            if (keywordQuery != null) b.must(keywordQuery);
            if (categoryQuery != null) b.filter(categoryQuery);
            if (priceQuery != null) b.filter(priceQuery);
            return b;
        });

        // Pagination
        int from = page * size;

        // Sorting
        SortOrder order = "desc".equalsIgnoreCase(sortDirection) ? SortOrder.Desc : SortOrder.Asc;

        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index("products")
                .query(boolQuery)
                .from(from)
                .size(size)
                .sort(so -> so.field(f -> f.field(sortField).order(order)))
        );

        // Execute search
        SearchResponse<ProductDocument> response = elasticsearchClient.search(searchRequest, ProductDocument.class);

        return response.hits().hits().stream()
                .map(hit -> hit.source())
                .collect(Collectors.toList());
    }

}
