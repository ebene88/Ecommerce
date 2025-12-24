package com.promotion.productSearchService.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.promotion.productSearchService.dto.ApiResponse;
import com.promotion.productSearchService.dto.PaginatedResponse;
import com.promotion.productSearchService.dto.ProductSearchDTO;
import com.promotion.productSearchService.model.ProductDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSearchService {

    private final ElasticsearchClient elasticsearchClient;

    // 🔹 Index or update a product
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

    // 🔹 Keyword search only
    public PaginatedResponse<ProductDocument> searchByKeyword(String keyword, int page, int size) throws IOException {
        int from = page * size;

        Query keywordQuery = QueryBuilders.multiMatch(m -> m
                .fields("name", "description", "category", "slug")
                .query(keyword)
        );

        SearchResponse<ProductDocument> response = elasticsearchClient.search(
                SearchRequest.of(s -> s
                        .index("products")
                        .query(keywordQuery)
                        .from(from)
                        .size(size)
                ),
                ProductDocument.class
        );

        List<ProductDocument> products = response.hits().hits().stream()
                .map(hit -> hit.source())
                .collect(Collectors.toList());

        long totalElements = response.hits().total() != null ? response.hits().total().value() : products.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return PaginatedResponse.<ProductDocument>builder()
                .content(products)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }

    // 🔹 Advanced filter search
    public PaginatedResponse<ProductDocument> searchProducts(
            ProductSearchDTO request
    ) throws IOException {

        final String keyword = request.getKeyword();
        final Long category = request.getCategoryId();
        final BigDecimal minPrice = request.getMinPrice();
        final BigDecimal maxPrice = request.getMaxPrice();
        final Map<String, Object> attributes = request.getAttributes();
        final int page = request.getPage();
        final int size = request.getSize();
        final String sortField = request.getSortField();
        final String sortDirection = request.getSortDirection();

        // Build BOOL query safely (no external mutable variables)
        Query boolQuery = QueryBuilders.bool(b -> {

            // 🔍 Keyword search
            if (keyword != null && !keyword.isBlank()) {
                b.must(QueryBuilders.multiMatch(m -> m
                        .fields("name", "description")
                        .query(keyword)
                ));
            }

            // 🗂 Category filter
            if (category != null ) {
                b.filter(QueryBuilders.term(t -> t
                        .field("categoryId")
                        .value(category)
                ));
            }

            // 💰 Price filter
            if (minPrice != null || maxPrice != null) {
                b.filter(QueryBuilders.range(r -> {
                    r.field("price");
                    if (minPrice != null) r.gte(JsonData.of(minPrice));
                    if (maxPrice != null) r.lte(JsonData.of(maxPrice));
                    return r;
                }));
            }

            // 🧩 Attribute filters (dynamic)
            if (attributes != null && !attributes.isEmpty()) {
                attributes.forEach((key, value) -> {
                    if (value instanceof String) {
                        b.filter(QueryBuilders.term(t ->
                                t.field("attributes." + key + ".keyword") // exact match
                                        .value((String) value)
                        ));
                    } else if (value instanceof Integer) {
                        b.filter(QueryBuilders.term(t ->
                                t.field("attributes." + key)
                                        .value((Integer) value)
                        ));
                    } else if (value instanceof Long) {
                        b.filter(QueryBuilders.term(t ->
                                t.field("attributes." + key)
                                        .value((Long) value)
                        ));
                    } else if (value instanceof Double) {
                        b.filter(QueryBuilders.term(t ->
                                t.field("attributes." + key)
                                        .value((Double) value)
                        ));
                    } else {
                        // fallback for other types
                        b.filter(QueryBuilders.term(t ->
                                t.field("attributes." + key + ".keyword")
                                        .value(value.toString())
                        ));
                    }
                });
            }




            // 🔄 Fallback → match all
            if ((keyword == null || keyword.isBlank())
                    && (category == null )
                    && minPrice == null
                    && maxPrice == null
                    && (attributes == null || attributes.isEmpty())) {

                b.must(QueryBuilders.matchAll(ma -> ma));
            }

            return b;
        });

        // Pagination
        int from = page * size;

        // Sorting
        SortOrder order = "desc".equalsIgnoreCase(sortDirection)
                ? SortOrder.Desc
                : SortOrder.Asc;

        // Search request
        SearchRequest searchRequest = SearchRequest.of(s -> {
            s.index("products")
                    .query(boolQuery)
                    .from(from)
                    .size(size);

            if (sortField != null && !sortField.isBlank()) {
                s.sort(so -> so.field(f -> f
                        .field(sortField)
                        .order(order)
                ));
            }
            return s;
        });

        // Execute
        SearchResponse<ProductDocument> response =
                elasticsearchClient.search(searchRequest, ProductDocument.class);

        List<ProductDocument> products = response.hits().hits().stream()
                .map(hit -> hit.source())
                .toList();

        long total = response.hits().total() != null
                ? response.hits().total().value()
                : products.size();

        int totalPages = (int) Math.ceil((double) total / size);

        return PaginatedResponse.<ProductDocument>builder()
                .content(products)
                .page(page)
                .size(size)
                .totalElements(total)
                .totalPages(totalPages)
                .build();
    }

}
