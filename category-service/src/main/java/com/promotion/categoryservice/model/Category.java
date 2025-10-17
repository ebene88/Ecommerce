package com.promotion.categoryservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories", indexes = {
        @Index(name = "idx_category_name", columnList = "name")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // simple and efficient

    @Column(unique = true, nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(unique = true)
    private String slug; // optional for URLs
}
