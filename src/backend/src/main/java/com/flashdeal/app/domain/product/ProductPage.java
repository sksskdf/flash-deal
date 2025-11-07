package com.flashdeal.app.domain.product;

import java.util.List;

import com.flashdeal.app.domain.common.PageInfo;

public record ProductPage(
    List<Product> content,
    PageInfo pageInfo
) {}
