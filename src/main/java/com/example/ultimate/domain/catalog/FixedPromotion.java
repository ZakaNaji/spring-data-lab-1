package com.example.ultimate.domain.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("FIXED")
@Getter
@Setter
@NoArgsConstructor
public class FixedPromotion extends Promotion {
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amountOff;
}
