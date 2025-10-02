package com.example.ultimate.repo;

import com.example.ultimate.domain.catalog.Category;
import com.example.ultimate.domain.catalog.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> findProductWithPriceBtwAndInCategoryAndNameContains(BigDecimal low,
                                                                                             BigDecimal high,
                                                                                             Category category,
                                                                                             String name) {

        return findProductWithNameContains(name)
                .and(findProductWithPriceBtw(low, high))
                .and(findProductInCategory(category));
    }

    public static Specification<Product> findProductWithNameContains(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<Product> findProductWithPriceBtw(BigDecimal low, BigDecimal high) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("price"), low, high));
    }

    public static Specification<Product> findProductInCategory(Category category) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("primaryCategory"), category);
    }
}
