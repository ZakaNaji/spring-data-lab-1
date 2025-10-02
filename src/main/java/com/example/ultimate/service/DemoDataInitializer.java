package com.example.ultimate.service;

import com.example.ultimate.domain.catalog.*;
import com.example.ultimate.domain.order.Order;
import com.example.ultimate.domain.order.OrderLine;
import com.example.ultimate.domain.order.OrderStatus;
import com.example.ultimate.domain.payment.CardPayment;
import com.example.ultimate.domain.payment.WalletPayment;
import com.example.ultimate.domain.user.UserAccount;
import com.example.ultimate.repo.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DemoDataInitializer {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void seedAll() {
        if (categoryRepository.count() > 0) {
            return; // data already present
        }

        Category books = createCategory("Books", null);
        Category games = createCategory("Games", null);
        Category tech = createCategory("Tech", null);
        Category smartphones = createCategory("Smartphones", tech);
        Category boardGames = createCategory("Board Games", games);

        Map<String, Product> products = new HashMap<>();
        products.put("BOOK-001", createProduct("BOOK-001", "Hibernate Mastery", new BigDecimal("49.99"), books, false, atUtc(2024, 1, 5, 8, 30)));
        products.put("BOOK-002", createProduct("BOOK-002", "Spring Data Recipes", new BigDecimal("39.90"), books, false, atUtc(2024, 2, 10, 12, 0)));
        products.put("GAME-777", createProduct("GAME-777", "Design Patterns Quest", new BigDecimal("59.99"), games, false, atUtc(2024, 2, 18, 19, 45)));
        products.put("GAME-101", createProduct("GAME-101", "Legacy of JPA", new BigDecimal("29.99"), boardGames, false, atUtc(2024, 2, 25, 16, 10)));
        products.put("TECH-500", createProduct("TECH-500", "Ultimate Phone XL", new BigDecimal("899.00"), smartphones, false, atUtc(2024, 3, 1, 9, 5)));
        products.put("BOOK-999", createProduct("BOOK-999", "Outdated Hibernate Guide", new BigDecimal("19.99"), books, true, atUtc(2023, 11, 11, 7, 20)));

        createInventory(products.get("BOOK-001"), 25);
        createInventory(products.get("BOOK-002"), 12);
        createInventory(products.get("GAME-777"), 4);
        createInventory(products.get("GAME-101"), 18);
        createInventory(products.get("TECH-500"), 6);
        createInventory(products.get("BOOK-999"), 0);

        createPromotions();

        UserAccount alice = userRepository.save(UserAccount.builder()
                .email("alice@example.com")
                .name("Alice Architect")
                .build());
        UserAccount bob = userRepository.save(UserAccount.builder()
                .email("bob@example.com")
                .name("Bob Builder")
                .build());
        UserAccount charlie = userRepository.save(UserAccount.builder()
                .email("charlie@example.com")
                .name("Charlie Analyst")
                .build());

        Order order1 = new Order();
        order1.setUser(alice);
        order1.setCreatedAt(atUtc(2024, 3, 15, 10, 15));
        order1.setStatus(OrderStatus.PAID);
        addLine(order1, products.get("BOOK-001"), 2);
        addLine(order1, products.get("GAME-777"), 1);
        order1.setTotal(products.get("BOOK-001").getPrice().multiply(BigDecimal.valueOf(2))
                .add(products.get("GAME-777").getPrice()));

        Order order2 = new Order();
        order2.setUser(bob);
        order2.setCreatedAt(atUtc(2024, 3, 20, 9, 0));
        order2.setStatus(OrderStatus.NEW);
        addLine(order2, products.get("TECH-500"), 1);
        order2.setTotal(products.get("TECH-500").getPrice());

        Order order3 = new Order();
        order3.setUser(alice);
        order3.setCreatedAt(atUtc(2024, 3, 25, 17, 0));
        order3.setStatus(OrderStatus.FULFILLED);
        addLine(order3, products.get("GAME-101"), 3);
        order3.setTotal(products.get("GAME-101").getPrice().multiply(BigDecimal.valueOf(3)));

        orderRepository.save(order1);
        orderRepository.save(order2);
        orderRepository.save(order3);

        createPayments(order1, order3);
    }

    private Category createCategory(String name, Category parent) {
        Category category = new Category();
        category.setName(name);
        category.setParent(parent);
        Category saved = categoryRepository.save(category);
        if (parent != null) {
            parent.getChildren().add(saved);
        }
        return saved;
    }

    private Product createProduct(String sku, String name, BigDecimal price, Category category, boolean deleted, OffsetDateTime createdAt) {
        Product product = Product.builder()
                .sku(sku)
                .name(name)
                .price(price)
                .primaryCategory(category)
                .deleted(deleted)
                .createdAt(createdAt)
                .build();
        return productRepository.save(product);
    }

    private void createInventory(Product product, int stock) {
        inventoryRepository.save(InventoryItem.builder()
                .product(product)
                .stock(stock)
                .build());
    }

    private void createPromotions() {
        PercentagePromotion springSale = new PercentagePromotion();
        springSale.setName("Spring Sale");
        springSale.setPercentOff(new BigDecimal("10.00"));
        entityManager.persist(springSale);

        FixedPromotion vipVoucher = new FixedPromotion();
        vipVoucher.setName("VIP Voucher");
        vipVoucher.setAmountOff(new BigDecimal("25.00"));
        entityManager.persist(vipVoucher);
    }

    private void addLine(Order order, Product product, int quantity) {
        OrderLine line = OrderLine.builder()
                .product(product)
                .quantity(quantity)
                .unitPrice(product.getPrice())
                .build();
        order.addLine(line);
    }

    private void createPayments(Order order1, Order order3) {
        CardPayment cardPayment = new CardPayment();
        cardPayment.setOrder(order1);
        cardPayment.setAmount(order1.getTotal());
        cardPayment.setPaidAt(atUtc(2024, 3, 15, 10, 30));
        cardPayment.setLast4("4242");
        paymentRepository.save(cardPayment);

        WalletPayment walletPayment = new WalletPayment();
        walletPayment.setOrder(order3);
        walletPayment.setAmount(order3.getTotal());
        walletPayment.setPaidAt(atUtc(2024, 3, 25, 17, 20));
        walletPayment.setProvider("PayBuddy");
        paymentRepository.save(walletPayment);
    }

    private OffsetDateTime atUtc(int year, int month, int day, int hour, int minute) {
        return OffsetDateTime.of(year, month, day, hour, minute, 0, 0, ZoneOffset.UTC);
    }
}
