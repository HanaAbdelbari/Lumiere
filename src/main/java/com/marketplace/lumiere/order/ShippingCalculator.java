package com.marketplace.lumiere.order;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Shipping rules (from the plan):
 *   - Free shipping for orders >= EGP 800
 *   - Otherwise: Cairo/Giza = EGP 70, rest of Egypt = EGP 90
 * The frontend only displays shipping; the backend is the source of truth.
 */
@Component
public class ShippingCalculator {

    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("800");
    private static final BigDecimal CAIRO_GIZA_FEE = new BigDecimal("70");
    private static final BigDecimal OTHER_FEE = new BigDecimal("90");

    // Governorate names that count as Cairo/Giza (lowercased for comparison).
    private static final Set<String> CAIRO_GIZA = Set.of("cairo", "giza", "القاهرة", "الجيزة");

    public BigDecimal calculate(BigDecimal productsTotal, String governorate) {
        // Free shipping over the threshold.
        if (productsTotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0) {
            return BigDecimal.ZERO;
        }
        String normalized = governorate == null ? "" : governorate.trim().toLowerCase();
        return CAIRO_GIZA.contains(normalized) ? CAIRO_GIZA_FEE : OTHER_FEE;
    }
}