package com.promotion.productSearchService.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggest;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggestOption;
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
import java.util.stream.Stream;


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

    public PaginatedResponse<ProductDocument> searchProducts(ProductSearchDTO request)
            throws IOException {

        final String keyword = request.getKeyword();
        final int categoryId = request.getCategoryId(); // 0 = no filter
        final BigDecimal minPrice = request.getMinPrice();
        final BigDecimal maxPrice = request.getMaxPrice();
        final Map<String, Object> attributes = request.getAttributes();
        final int page = request.getPage();
        final int size = request.getSize();
        final String sortField = request.getSortField();
        final String sortDirection = request.getSortDirection();

        Query boolQuery = QueryBuilders.bool(b -> {

            boolean hasAnyFilter = false;

            // 🔍 Keyword
            if (keyword != null && !keyword.isBlank()) {
                hasAnyFilter = true;
                b.must(m -> m.multiMatch(mm -> mm
                        .fields("name", "description")
                        .query(keyword)
                ));
            }

            // 🗂 Category filter (FIXED)
            if (categoryId > 0) {
                hasAnyFilter = true;
                b.filter(f -> f.term(t -> t
                        .field("categoryId")
                        .value(categoryId)
                ));
            }

            // 💰 Price filter
            if (minPrice != null || maxPrice != null) {
                hasAnyFilter = true;
                b.filter(f -> f.range(r -> {
                    r.field("price");
                    if (minPrice != null) r.gte(JsonData.of(minPrice));
                    if (maxPrice != null) r.lte(JsonData.of(maxPrice));
                    return r;
                }));
            }

            // 🧩 Attributes filter
            if (attributes != null && !attributes.isEmpty()) {
                hasAnyFilter = true;
                attributes.forEach((key, value) -> {

                    String field = "attributes." + key;
                    if (value instanceof String) {
                        field += ".keyword";
                    }

                    String finalField = field;
                    b.filter(f -> f.term(t -> t
                            .field(finalField)
                            .value(value.toString())
                    ));
                });
            }

            // 🔄 No filters → match all
            if (!hasAnyFilter) {
                b.must(m -> m.matchAll(ma -> ma));
            }

            return b;
        });

        int from = page * size;

        SortOrder order = "desc".equalsIgnoreCase(sortDirection)
                ? SortOrder.Desc
                : SortOrder.Asc;

        SearchResponse<ProductDocument> response =
                elasticsearchClient.search(
                        SearchRequest.of(s -> {
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
                        }),
                        ProductDocument.class
                );

        List<ProductDocument> products = response.hits().hits()
                .stream()
                .map(Hit::source)
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


    public List<String> suggestByPrefix(String prefix) throws IOException {
        if (prefix == null || prefix.length() < 2) {
            return List.of();
        }

        SearchResponse<ProductDocument> response = elasticsearchClient.search(s -> s
                        .index("products")
                        .query(q -> q
                                .bool(b -> b
                                        // search prefix on name or description
                                        .should(m -> m
                                                .matchPhrasePrefix(mp -> mp
                                                        .field("name")
                                                        .query(prefix)
                                                )
                                        )
                                        .should(m -> m
                                                .matchPhrasePrefix(mp -> mp
                                                        .field("description")
                                                        .query(prefix)
                                                )
                                        )
                                )
                        )
                        .size(10)  // limit suggestions
                        .source(src -> src.filter(f -> f.includes("name"))),  // get only name field
                ProductDocument.class
        );

        return response.hits().hits().stream()
                .map(hit -> hit.source().getName())
                .distinct()
                .toList();
    }


}
