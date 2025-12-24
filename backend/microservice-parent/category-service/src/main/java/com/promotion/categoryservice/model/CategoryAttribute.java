package com.promotion.categoryservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "category_attributes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String code;   // ram, cpu, screen_size

    @Column(nullable = false)
    private String label;  // RAM, Processor, Screen Size

    @Column(nullable = false)
    private String type;   // string, number, boolean

    private boolean filterable;
    private boolean searchable;
}
