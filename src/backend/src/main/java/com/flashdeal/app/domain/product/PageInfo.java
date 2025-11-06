package com.flashdeal.app.domain.product;

public record PageInfo(
    int page,
    int size,
    long total,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious
) {}