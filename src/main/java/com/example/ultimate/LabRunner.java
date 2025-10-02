package com.example.ultimate;

import com.example.ultimate.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LabRunner {

    @Bean
    CommandLineRunner demo(
            CatalogService catalogService,
            CheckoutService checkoutService,
            ReportingService reportingService) {
        return args -> {
            // Enable one exercise at a time. Follow README tasks.
            catalogService.seedDemoCatalog(); // EX-0 seed data

            // EX-1: implement soft delete & query filters, then run:
            System.out.println("--- Ex1 ---");
            catalogService.discontinueSomeProductsAndListActiveOnes();

            // EX-2: specifications + projections paging:
            // catalogService.searchCatalogDemo();

            // EX-3: solve N+1 via entity graphs / fetch-join:
            // catalogService.listProductsWithCategoriesDemo();

            // EX-4: checkout flow with optimistic locking on stock:
            // checkoutService.simulateConcurrentCheckouts();

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
