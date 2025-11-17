package com.software.backend.dto.request;

public record ProductSearchRequest(
        String name,
        Long quantityMin,
        Long quantityMax,
        Double priceMin,
        Double priceMax,
        Long categoryId
) {
}
