package com.example.ultimate.service;

import com.example.ultimate.domain.catalog.InventoryItem;
import com.example.ultimate.domain.catalog.Product;
import com.example.ultimate.domain.order.*;
import com.example.ultimate.domain.user.UserAccount;
import com.example.ultimate.repo.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.concurrent.*;

@Service
@Log4j2
public class CheckoutService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;
    private final TransactionTemplate txTemplate;

    @PersistenceContext
    private EntityManager em;

    public CheckoutService(UserRepository userRepository, ProductRepository productRepository, InventoryRepository inventoryRepository, OrderRepository orderRepository, TransactionTemplate txTemplate) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderRepository = orderRepository;
        this.txTemplate = txTemplate;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void simulatePessimisticLockContention(Long productId) {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        Callable<Void> t1 = () -> {
            ready.countDown();
            start.await();
            txTemplate.execute(status -> {
                InventoryItem inv = inventoryRepository.lockByProductId(productId).orElseThrow();
                // hold the row lock so the second transaction must wait
                inv.setStock(inv.getStock() - 1);
                em.flush(); // sends SELECT ... FOR UPDATE and the update
                try { Thread.sleep(1_000L); } catch (InterruptedException ignored) {}
                return null;
            });
            return null;
        };

        Callable<Void> t2 = () -> {
            ready.countDown();
            start.await();
            long t0 = System.nanoTime();
            txTemplate.execute(status -> {
                InventoryItem inv = inventoryRepository.lockByProductId(productId).orElseThrow();
                inv.setStock(inv.getStock() - 1);
                em.flush();
                return null;
            });
            long waitedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t0);
            log.info("Second transaction waited ~{} ms for the pessimistic lock", waitedMs);
            return null;
        };

        try {
            Future<Void> f1 = pool.submit(t1);
            Future<Void> f2 = pool.submit(t2);

            // ensure both threads are ready, then start at the same time
            ready.await();
            start.countDown();

            f1.get();
            f2.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException("Pessimistic lock simulation failed", e.getCause());
        } finally {
            pool.shutdown();
        }
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
