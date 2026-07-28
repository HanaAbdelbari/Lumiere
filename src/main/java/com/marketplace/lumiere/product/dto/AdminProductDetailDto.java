package com.marketplace.lumiere.product.dto;

import com.marketplace.lumiere.product.Product;
import com.marketplace.lumiere.product.ProductImage;

import java.math.BigDecimal;
import java.util.List;

// Full product data for the admin edit form (includes categoryId, displayOrder,
// and all image URLs — everything the form needs to pre-fill).
public record AdminProductDetailDto(
        Long id,
        Long categoryId,
        String name,
        String slug,
        String description,
        BigDecimal price,
        BigDecimal salePrice,
        String material,
        String size,
        String chainLength,
        Integer stockQuantity,
        Integer displayOrder,
        Boolean isActive,
        List<String> images
) {
    public static AdminProductDetailDto from(Product p) {
        List<String> imageUrls = p.getImages().stream()
                .map(ProductImage::getImageUrl)
                .toList();
        return new AdminProductDetailDto(
                p.getId(),
                p.getCategory() != null ? p.getCategory().getId() : null,
                p.getName(),
                p.getSlug(),
                p.getDescription(),
                p.getPrice(),
                p.getSalePrice(),
                p.getMaterial(),
                p.getSize(),
                p.getChainLength(),
                p.getStockQuantity(),
                p.getDisplayOrder(),
                p.getIsActive(),
                imageUrls
        );
    }
}