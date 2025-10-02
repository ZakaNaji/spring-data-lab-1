package com.example.ultimate.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "card_payments")
@Getter
@Setter
@NoArgsConstructor
public class CardPayment extends Payment {
    @Column(nullable = false, length = 4)
    private String last4;
}
