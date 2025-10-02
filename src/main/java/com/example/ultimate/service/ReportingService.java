package com.example.ultimate.service;

import com.example.ultimate.repo.PaymentRepository;
import com.example.ultimate.repo.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportingService {

    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;

    public ReportingService(PaymentRepository paymentRepository, ProductRepository productRepository) {
        this.paymentRepository = paymentRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public void listPromotionsAndPayments() {
        // --- INHERIT-1 ---
        // paymentRepository.findAll().forEach(p -> System.out.println(p.getClass().getSimpleName() + " id=" + p.getId()));
    }
}
