package com.example.ultimate.repo;

import com.example.ultimate.domain.catalog.InventoryItem;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {

    // Pessimistic lock for checkout
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from InventoryItem i where i.product.id = :productId")
    Optional<InventoryItem> lockByProductId(@Param("productId") Long productId);

    // --- OPTLOCK-1 ---
    // TODO: Alternatively, show optimistic stock decrement relying on @Version
}
