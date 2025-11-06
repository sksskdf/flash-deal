package com.flashdeal.app.domain.product;

import java.math.BigDecimal;

public record ProductFilter(
    DealStatus status,
    String category,
    BigDecimal minPrice,
    BigDecimal maxPrice,
    Integer minDiscountRate,
    String searchText
) {}
