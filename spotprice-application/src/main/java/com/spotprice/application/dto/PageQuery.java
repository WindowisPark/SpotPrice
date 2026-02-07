package com.spotprice.application.dto;

public record PageQuery(int page, int size) {

    public PageQuery {
        if (page < 0) throw new IllegalArgumentException("page must be >= 0");
        if (size < 1 || size > 100) throw new IllegalArgumentException("size must be 1~100");
    }

    public static PageQuery of(int page, int size) {
        return new PageQuery(page, size);
    }
}
