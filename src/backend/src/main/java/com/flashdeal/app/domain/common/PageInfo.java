package com.flashdeal.app.domain.common;

public record PageInfo(
    int page,
    int size,
    long total,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious
) {}