package com.marketplace.lumiere.product.dto;

import com.marketplace.lumiere.product.Product;
import com.marketplace.lumiere.product.ProductImage;

import java.math.BigDecimal;

/**
 * Compact product shape for cards (shop, new arrivals, offers, related).
 * Carries a single main image only — cards never show a gallery.
 */
public record ProductSummaryDto(
        Long id,
        String name,
        String slug,
        BigDecimal price,
        BigDecimal salePrice,
        boolean onSale,
        Integer discountPercent,
        boolean inStock,
        Integer stockQuantity,
        String mainImageUrl
) {
    public static ProductSummaryDto from(Product p) {
        String mainImage = p.getImages().isEmpty()
                ? null
                : p.getImages().get(0).getImageUrl(); // images are ordered by display_order, so [0] is main

        int stock = p.getStockQuantity() != null ? p.getStockQuantity() : 0;

        return new ProductSummaryDto(
                p.getId(),
                p.getName(),
                p.getSlug(),
                p.getPrice(),
                p.getSalePrice(),
                p.isOnSale(),
                discountPercent(p),
                stock > 0,
                stock,
                mainImage
        );
    }

    // Computed, never stored — same rule as the schema.
    private static Integer discountPercent(Product p) {
        if (!p.isOnSale() || p.getSalePrice() == null) {
            return null;
        }
        BigDecimal off = p.getPrice().subtract(p.getSalePrice());
        return off.multiply(BigDecimal.valueOf(100))
                .divide(p.getPrice(), 0, java.math.RoundingMode.HALF_UP)
                .intValue();
    }
}