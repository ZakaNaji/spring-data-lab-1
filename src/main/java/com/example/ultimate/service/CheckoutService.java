package com.example.ultimate.service;

import com.example.ultimate.domain.catalog.InventoryItem;
import com.example.ultimate.domain.catalog.Product;
import com.example.ultimate.domain.order.*;
import com.example.ultimate.domain.user.UserAccount;
import com.example.ultimate.repo.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class CheckoutService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;

    @PersistenceContext
    private EntityManager em;

    public CheckoutService(UserRepository userRepository, ProductRepository productRepository, InventoryRepository inventoryRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void simulateConcurrentCheckouts() {
        // --- OPTLOCK-1 ---
        // Demonstrate optimistic locking by decrementing the same stock twice (in threads or sequential with stale versions)
        // InventoryItem inv = inventoryRepository.lockByProductId(productId).orElseThrow();
        // inv.setStock(inv.getStock() - 1);
        // em.flush(); // version should increment; a concurrent attempt should fail
    }

    @Transactional
    public Order createOrder(Long userId, Long productId, int qty) {
        UserAccount user = userRepository.findById(userId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();

        // Reserve inventory (pessimistic lock example)
        InventoryItem inv = inventoryRepository.lockByProductId(productId).orElseThrow();
        if (inv.getStock() < qty) throw new IllegalStateException("Out of stock");
        inv.setStock(inv.getStock() - qty);

        Order order = Order.builder()
                .user(user)
                .createdAt(OffsetDateTime.now())
                .status(OrderStatus.NEW)
                .build();

        OrderLine line = OrderLine.builder()
                .product(product)
                .quantity(qty)
                .unitPrice(product.getPrice())
                .build();

        order.addLine(line);
        order.setTotal(product.getPrice().multiply(BigDecimal.valueOf(qty)));
        return orderRepository.save(order);
    }
}
