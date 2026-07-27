package com.marketplace.lumiere.order.dto;

import java.util.List;

// What the frontend sends to create an order.
// Note: it sends product id + quantity only — NOT prices.
// Prices are looked up in the backend from the database (security: the client
// must not be able to set its own prices).
public record CreateOrderRequest(
        String fullName,
        String phone,
        String governorate,
        String address,
        String notes,
        List<OrderItemRequest> items
) {
    public record OrderItemRequest(
            Long productId,
            Integer quantity
    ) {
    }
}