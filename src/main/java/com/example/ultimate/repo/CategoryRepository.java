package com.example.ultimate.repo;

import com.example.ultimate.domain.catalog.Category;
import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // --- TREE-1 ---
    // TODO: Fetch a category with children in one roundtrip (avoid N+1).
    // @EntityGraph(attributePaths = "children")
    // Optional<Category> findWithChildrenById(Long id);

    Optional<Category> findByName(String name);
}
