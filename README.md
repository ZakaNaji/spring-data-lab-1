# Ultimate JPA/Hibernate Lab — Data Layer Mastery

**Goal:** You deliver production-grade repository methods & query strategies across a realistic e-commerce domain. Entities & services are scaffolded; you implement **ALL data-layer pieces** (queries, specs, projections, pagination, locking, bulk ops, fetch plans, soft deletes).

## Domain

- **Catalog**: `Product` (soft delete, audit-ish, @Version), `Category` (tree), `InventoryItem` (stock + @Version), `Promotion` (SINGLE_TABLE)
- **Orders**: `Order` + `OrderLine`
- **Payments**: `Payment` (JOINED: Card/Wallet)
- **Users**: `UserAccount`

## Run

```bash
mvn spring-boot:run
# MySQL-like profile (IDENTITY behavior)
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

Enable exercises in `LabRunner` one by one.

---

## Exercises

### EX-0 Seed
- Run `catalogService.seedDemoCatalog()`.

### EX-1 Soft delete (**SOFT-DELETE-1**)
- Ensure products with `deleted=true` never appear in normal reads.
- Option A: Hibernate `@Where(clause = "deleted=false")` (+ optional `@SQLDelete` for updates).
- Option B: explicit repository methods `...DeletedFalse`.

### EX-2 Specifications + Projections (**SPEC-1**, **DTO-1**)
- Build dynamic spec: price range, name contains, category in.
- Implement `ProductRepository.listItems(Pageable)` returning `ProductListItem`.
- Provide `countQuery` if needed.

### EX-3 N+1 on categories (**N+1-1**)
- Implement an `@EntityGraph` method to fetch `primaryCategory` for latest products.

### EX-4 Concurrency on inventory (**OPTLOCK-1**)
- Demonstrate optimistic lock conflict on `InventoryItem` when decrementing stock from two threads.

### EX-5 Pagination + fetch-join (**FETCHJOIN-PAGE-1**)
- Implement `OrderRepository.pageOrdersWithLines(userId, pageable)` with `fetch join` for content and a clean `countQuery`.

### EX-6 Bulk ops & PC sync (**BULK-1**)
- Implement bulk price drop with `@Modifying(clearAutomatically = true)` and show stale-state avoidance.

### EX-7 Trees (**TREE-1**)
- Fetch `Category` with children using `@EntityGraph` and compare to lazy N+1.

### EX-8 Inheritance (**INHERIT-1**)
- Query `Promotion` (SINGLE_TABLE) vs `Payment` (JOINED) and observe SQL patterns.

---

## Notes

- SQL & bind logs are enabled for learning.
- Uncomment `hibernate.jdbc.batch_size` to experiment with insert batching.
- Add custom repository fragments if you prefer cross-cutting behaviors (e.g., soft-delete base class).
