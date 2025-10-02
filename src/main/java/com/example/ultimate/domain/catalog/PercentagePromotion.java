package com.example.ultimate.domain.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("PERCENT")
@Getter
@Setter
@NoArgsConstructor
public class PercentagePromotion extends Promotion {
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal percentOff;
}
