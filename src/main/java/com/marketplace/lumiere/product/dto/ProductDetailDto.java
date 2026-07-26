package com.marketplace.lumiere.product.dto;

import com.marketplace.lumiere.product.Product;
import com.marketplace.lumiere.product.ProductImage;

import java.math.BigDecimal;
import java.util.List;

/**
 * Full product shape for the product details page.
 * Carries all images (the gallery) and the optional attributes.
 * Attributes are null when absent — the frontend hides them (schema rule).
 */
public record ProductDetailDto(
        Long id,
        String name,
        String slug,
        String description,
        BigDecimal price,
        BigDecimal salePrice,
        boolean onSale,
        Integer discountPercent,
        String material,
        String size,
        String chainLength,
        Integer stockQuantity,
        boolean inStock,
        String categoryName,
        String categorySlug,
        List<String> images
) {
    public static ProductDetailDto from(Product p) {
        List<String> imageUrls = p.getImages().stream()
                .map(ProductImage::getImageUrl)
                .toList(); // already ordered by display_order

        return new ProductDetailDto(
                p.getId(),
                p.getName(),
                p.getSlug(),
                p.getDescription(),
                p.getPrice(),
                p.getSalePrice(),
                p.isOnSale(),
                discountPercent(p),
                p.getMaterial(),
                p.getSize(),
                p.getChainLength(),
                p.getStockQuantity(),
                p.getStockQuantity() > 0,
                p.getCategory().getName(),
                p.getCategory().getSlug(),
                imageUrls
        );
    }

    private static Integer discountPercent(Product p) {
        if (!p.isOnSale()) {
            return null;
        }
        BigDecimal off = p.getPrice().subtract(p.getSalePrice());
        return off.multiply(BigDecimal.valueOf(100))
                .divide(p.getPrice(), 0, java.math.RoundingMode.HALF_UP)
                .intValue();
    }
}