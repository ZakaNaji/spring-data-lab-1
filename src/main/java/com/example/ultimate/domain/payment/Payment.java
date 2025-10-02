package com.example.ultimate.domain.payment;

import com.example.ultimate.domain.order.Order;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter @Setter
@Table(name = "payments")
public abstract class Payment {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true)
    private Order order;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private OffsetDateTime paidAt;
}
