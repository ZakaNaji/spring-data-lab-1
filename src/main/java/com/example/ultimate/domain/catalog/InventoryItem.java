package com.example.ultimate.domain.catalog;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "inventory")
public class InventoryItem {
    @Id
    private Long productId; // 1:1 with product

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private int stock;

    @Version
    private Long version; // for optimistic locking
}
