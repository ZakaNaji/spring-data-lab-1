package com.example.ultimate;

import com.example.ultimate.domain.catalog.Product;
import com.example.ultimate.repo.ProductRepository;
import com.example.ultimate.service.CatalogService;
import com.example.ultimate.service.CheckoutService;
import com.example.ultimate.service.DemoDataInitializer;
import com.example.ultimate.service.ReportingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LabRunner {

    @Bean
    CommandLineRunner demo(
            DemoDataInitializer dataInitializer,
            ProductRepository productRepository,
            CatalogService catalogService,
            CheckoutService checkoutService,
            ReportingService reportingService) {
        return args -> {
            // Seed once for all exercises (safe to call repeatedly).
            dataInitializer.seedAll();

            // EX-1: implement soft delete & query filters, then run:
            //catalogService.discontinueSomeProductsAndListActiveOnes();

            // EX-2: specifications + projections paging:
            //catalogService.searchCatalogDemo();

            // EX-3: solve N+1 via entity graphs / fetch-join:
            //catalogService.listProductsWithCategoriesDemo();

            // EX-4: checkout flow with pessimistic locking on stock:
            System.out.println("--- Ex4 ---");
            Product lockingProduct = productRepository.findBySkuAndDeletedFalse("BOOK-001")
                    .orElseThrow(() -> new IllegalStateException("Seed data missing BOOK-001"));
            checkoutService.simulatePessimisticLockContention(lockingProduct.getId());

            // EX-5: pagination with fetch-join + custom countQuery:
            // catalogService.pageOrdersWithLinesDemo();

            // EX-6: bulk update & clearAutomatically:
            // catalogService.applyBulkPriceDrop();

            // EX-7: trees:
            // catalogService.treeFetchDemo();

            // EX-8: inheritance queries (promotion/payment):
            // reportingService.listPromotionsAndPayments();
        };
    }
}
