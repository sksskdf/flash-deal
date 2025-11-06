package com.flashdeal.app.domain.product;

public record SortOption(
    ProductSortField field,
    SortOrder order
) {
}
