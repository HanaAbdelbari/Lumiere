package com.marketplace.lumiere.product.dto;

import java.math.BigDecimal;
import java.util.List;

// What the admin form sends to create or update a product.
// imageUrls: list of image URLs (for now typed/pasted; later uploaded via Cloudinary).
public record ProductRequest(
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
        List<String> imageUrls
) {
}