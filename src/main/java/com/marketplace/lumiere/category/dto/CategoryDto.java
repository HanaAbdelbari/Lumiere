package com.marketplace.lumiere.category.dto;

import com.marketplace.lumiere.category.Category;

public record CategoryDto(
        Long id,
        String name,
        String slug,
        String coverImage
) {
    public static CategoryDto from(Category c) {
        return new CategoryDto(
                c.getId(),
                c.getName(),
                c.getSlug(),
                c.getCoverImage()
        );
    }
}