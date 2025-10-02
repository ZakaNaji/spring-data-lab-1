package com.example.ultimate.repo;

import com.example.ultimate.domain.order.Order;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // --- FETCHJOIN-PAGE-1 ---
    // TODO: Page orders with lines pre-fetched; provide custom countQuery.
    // @Query(value = "select distinct o from Order o left join fetch o.lines where o.user.id = :userId",
    //        countQuery = "select count(o) from Order o where o.user.id = :userId")
    // Page<Order> pageOrdersWithLines(@Param("userId") Long userId, Pageable pageable);
}
