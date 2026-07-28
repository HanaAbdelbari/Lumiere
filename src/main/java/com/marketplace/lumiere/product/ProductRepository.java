package com.marketplace.lumiere.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Admin: all products (including hidden/inactive), newest first.
    List<Product> findAllByOrderByCreatedAtDesc();

    // Product details page: look up by slug. Only active products are shown.
    Optional<Product> findBySlugAndIsActiveTrue(String slug);

    // Shop page: all active products, ordered for display.
    List<Product> findByIsActiveTrueOrderByDisplayOrderAscCreatedAtDesc();

    // Category page: active products in one category.
    List<Product> findByCategorySlugAndIsActiveTrueOrderByDisplayOrderAsc(String categorySlug);

    // Home — New Arrivals: newest active products (limit applied in service).
    List<Product> findByIsActiveTrueOrderByCreatedAtDesc();

    // Home — Offers / Sale page: active products actually on sale
    // (sale_price present AND lower than price). Computed in the query so the
    // DB does the filtering, matching the "on sale is computed" rule.
    @Query("""
            SELECT p FROM Product p
            WHERE p.isActive = true
              AND p.salePrice IS NOT NULL
              AND p.salePrice < p.price
            ORDER BY p.displayOrder ASC
            """)
    List<Product> findOnSale();
}