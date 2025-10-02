package com.example.ultimate.service.dto;

import java.math.BigDecimal;

public record ProductListItem(Long id, String sku, String name, BigDecimal price, String categoryName) {}
