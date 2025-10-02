package com.example.ultimate.domain.catalog;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "promo_type")
@Getter @Setter
@Table(name = "promotions")
public abstract class Promotion {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;
}

@Entity
@DiscriminatorValue("PERCENT")
class PercentagePromotion extends Promotion {
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal percentOff;
}

@Entity
@DiscriminatorValue("FIXED")
class FixedPromotion extends Promotion {
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amountOff;
}
