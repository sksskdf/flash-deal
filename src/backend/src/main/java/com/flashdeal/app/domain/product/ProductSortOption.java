package com.flashdeal.app.domain.product;

import com.flashdeal.app.domain.common.SortOrder;

public record ProductSortOption(
    ProductSortField field,
    SortOrder order
) {
}
