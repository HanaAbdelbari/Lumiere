package com.marketplace.lumiere.order;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;


@Component
public class ShippingCalculator {

    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("800");

    private static final BigDecimal CAIRO_GIZA_FEE = new BigDecimal("85");
    private static final BigDecimal CANAL_DAMIETTA_FEE = new BigDecimal("95");
    private static final BigDecimal UPPER_EGYPT_FEE = new BigDecimal("110");
    private static final BigDecimal DEFAULT_FEE = new BigDecimal("90");

    private static final Set<String> CAIRO_GIZA = Set.of(
            "cairo", "giza", "القاهرة", "الجيزة"
    );

    private static final Set<String> CANAL_DAMIETTA = Set.of(
            "ismailia", "suez", "port said", "damietta",
            "الإسماعيلية", "السويس", "بورسعيد", "دمياط"
    );

    private static final Set<String> UPPER_EGYPT = Set.of(
            "fayoum", "beni suef", "minya", "assiut", "sohag", "qena", "luxor", "aswan",
            "الفيوم", "بني سويف", "المنيا", "أسيوط", "سوهاج", "قنا", "الأقصر", "أسوان"
    );

    public BigDecimal calculate(BigDecimal productsTotal, String governorate) {
        // Free shipping over the threshold.
        if (productsTotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0) {
            return BigDecimal.ZERO;
        }

        if (governorate == null || governorate.isBlank()) {
            return DEFAULT_FEE;
        }

        String normalized = governorate.trim().toLowerCase();

        if (CAIRO_GIZA.contains(normalized)) {
            return CAIRO_GIZA_FEE;
        }
        if (CANAL_DAMIETTA.contains(normalized)) {
            return CANAL_DAMIETTA_FEE;
        }
        if (UPPER_EGYPT.contains(normalized)) {
            return UPPER_EGYPT_FEE;
        }

        return DEFAULT_FEE;
    }
}