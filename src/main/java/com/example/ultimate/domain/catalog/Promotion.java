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
