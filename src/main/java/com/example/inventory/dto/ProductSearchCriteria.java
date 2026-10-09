package com.example.inventory.dto;

import java.math.BigDecimal;

public record ProductSearchCriteria(
        String query,
        String category,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        int page,
        int size
) {}
