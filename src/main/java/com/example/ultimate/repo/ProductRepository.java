package com.example.ultimate.repo;

import com.example.ultimate.domain.catalog.Product;
import com.example.ultimate.service.dto.ProductListItem;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    // --- SOFT-DELETE-1 ---
    Optional<Product> findBySkuAndDeletedFalse(String sku);
    @EntityGraph(attributePaths = {"primaryCategory"})
    Page<Product> findAllByDeletedFalse(Pageable pageable);

    // --- N+1-1 ---
    // TODO: Fetch category in one query: choose @EntityGraph(attributePaths="primaryCategory") or a JPQL fetch-join.
    // @EntityGraph(attributePaths = "primaryCategory")
    // List<Product> findTop50ByDeletedFalseOrderByCreatedAtDesc();

    // --- DTO-1 ---
    // TODO: Provide a lightweight projection for listings
    @Query("select new com.example.ultimate.service.dto.ProductListItem(p.id, p.sku, p.name, p.price, c.name) " +
           "from Product p left join p.primaryCategory c where p.deleted=false")
    Page<ProductListItem> listItems(Pageable pageable);

    // --- BULK-1 ---
    // TODO: Bulk price drop. Use clearAutomatically to avoid stale PC.
    @Modifying(clearAutomatically = true)
    @Query("update Product p set p.price = p.price * (1 - :ratio) where p.deleted=false and p.primaryCategory.id in :categoryIds")
    int applyPriceDrop(@Param("ratio") double ratio, @Param("categoryIds") List<Long> categoryIds);
}
