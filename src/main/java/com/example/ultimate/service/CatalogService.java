package com.example.ultimate.service;

import com.example.ultimate.domain.catalog.*;
import com.example.ultimate.repo.*;
import com.example.ultimate.service.dto.ProductListItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class CatalogService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;

    @PersistenceContext
    private EntityManager em;

    public CatalogService(ProductRepository productRepository, CategoryRepository categoryRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public void seedDemoCatalog() {
        Category books = categoryRepository.save(Category.builder().name("Books").build());
        Category games = categoryRepository.save(Category.builder().name("Games").build());

        Product p1 = productRepository.save(Product.builder()
                .sku("BOOK-001").name("Hibernate Mastery")
                .price(new BigDecimal("49.99"))
                .primaryCategory(books)
                .createdAt(OffsetDateTime.now())
                .build());

        Product p2 = productRepository.save(Product.builder()
                .sku("GAME-777").name("Design Patterns Quest")
                .price(new BigDecimal("59.99"))
                .primaryCategory(games)
                .createdAt(OffsetDateTime.now())
                .build());

        inventoryRepository.save(InventoryItem.builder().product(p1).stock(10).build());
        inventoryRepository.save(InventoryItem.builder().product(p2).stock(3).build());
    }

    @Transactional
    public void discontinueSomeProductsAndListActiveOnes() {
        // --- SOFT-DELETE-1 ---
        Product p = productRepository.findBySkuAndDeletedFalse("BOOK-001").orElseThrow();
        p.setDeleted(true);
        em.flush();
        Page<Product> page = productRepository.findAllByDeletedFalse(PageRequest.of(0, 10));
        System.out.println("Active products: " + page.getContent().size());
    }

    @Transactional(readOnly = true)
    public void searchCatalogDemo() {
        // --- SPEC-1 + DTO-1 ---
        // Build a dynamic Specification: priceBetween, categoryIn, nameContains
        //var cat = categoryRepository.findByName("Books").get();
        //var low = BigDecimal.valueOf(30);
        //var high = BigDecimal.valueOf(50);
        //Page<Product> page = productRepository.findAll(
        //        ProductSpecification.findProductWithPriceBtwAndInCategoryAndNameContains(low, high, cat, "Hibernate"),
        //        PageRequest.of(0, 10));
        Page<ProductListItem> page = productRepository.listItems(PageRequest.of(0, 10));
        System.out.println("Items page size = " + page.getContent().size());
        System.out.println(page.getContent().get(0));
    }

    @Transactional(readOnly = true)
    public void listProductsWithCategoriesDemo() {
        // --- N+1-1 ---
        // Call your @EntityGraph method to fetch category eagerly for latest products
        // List<Product> latest = productRepository.findAllByDeletedFalse(PageRequest.of(0, 50, Sort.by("createdAt").descending())).getContent();
        // latest.forEach(p -> System.out.println(p.getName() + " / " + (p.getPrimaryCategory() != null ? p.getPrimaryCategory().getName() : "-")));
    }

    @Transactional
    public void applyBulkPriceDrop() {
        // --- BULK-1 ---
        // var cat = categoryRepository.findByName("Books").orElseThrow();
        // int updated = productRepository.applyPriceDrop(0.10, List.of(cat.getId()));
        // System.out.println("Price updated rows: " + updated);
    }

    @Transactional(readOnly = true)
    public void pageOrdersWithLinesDemo() {
        // Implement in OrderRepository and call from CheckoutService once there are orders
    }

    @Transactional(readOnly = true)
    public void treeFetchDemo() {
        // --- TREE-1 ---
        // var root = categoryRepository.findWithChildrenById( /* some id */ 1L);
        // root.ifPresent(c -> System.out.println("Children: " + c.getChildren().size()));
    }
}
