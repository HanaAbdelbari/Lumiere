package com.marketplace.lumiere.order.dto;

import com.marketplace.lumiere.order.Order;

import java.math.BigDecimal;

// What the backend returns after creating an order — used by the success page.
public record OrderResponse(
        String orderNumber,
        String status,
        BigDecimal productsTotal,
        BigDecimal shippingFee,
        BigDecimal totalAmount,
        BigDecimal depositAmount
) {
    public static OrderResponse from(Order order) {
        // products total = total - shipping
        BigDecimal productsTotal = order.getTotalAmount().subtract(order.getShippingFee());
        return new OrderResponse(
                order.getOrderNumber(),
                order.getStatus().name(),
                productsTotal,
                order.getShippingFee(),
                order.getTotalAmount(),
                order.getDepositAmount()
        );
    }
}