package com.marketplace.lumiere.product.dto;

import com.marketplace.lumiere.product.Product;

import java.math.BigDecimal;

// Product row for the admin list (includes inactive/hidden products).
public record AdminProductDto(
        Long id,
        String name,
        String slug,
        BigDecimal price,
        BigDecimal salePrice,
        Integer stockQuantity,
        Boolean isActive,
        String categoryName,
        String mainImageUrl
) {
    public static AdminProductDto from(Product p) {
        String mainImage = p.getImages().isEmpty()
                ? null
                : p.getImages().get(0).getImageUrl();
        return new AdminProductDto(
                p.getId(),
                p.getName(),
                p.getSlug(),
                p.getPrice(),
                p.getSalePrice(),
                p.getStockQuantity(),
                p.getIsActive(),
                p.getCategory() != null ? p.getCategory().getName() : null,
                mainImage
        );
    }
}