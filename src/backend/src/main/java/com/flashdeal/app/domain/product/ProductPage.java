package com.flashdeal.app.domain.product;

import java.util.List;

public record ProductPage(
    List<Product> content,
    PageInfo pageInfo
) {}
