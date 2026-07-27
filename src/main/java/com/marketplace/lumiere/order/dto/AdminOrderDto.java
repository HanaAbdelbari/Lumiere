package com.marketplace.lumiere.order.dto;

import com.marketplace.lumiere.order.Order;
import com.marketplace.lumiere.order.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Full order view for the admin dashboard.
public record AdminOrderDto(
        Long id,
        String orderNumber,
        String status,
        BigDecimal totalAmount,
        BigDecimal shippingFee,
        BigDecimal depositAmount,
        LocalDateTime createdAt,
        LocalDateTime confirmedAt,
        // Customer
        String customerName,
        String phone,
        String governorate,
        String address,
        String notes,
        // Items
        List<AdminOrderItemDto> items
) {
    public record AdminOrderItemDto(
            String productName,
            Integer quantity,
            BigDecimal unitPrice
    ) {
    }

    public static AdminOrderDto from(Order order) {
        var customer = order.getCustomerInfo();
        List<AdminOrderItemDto> itemDtos = order.getItems().stream()
                .map(AdminOrderDto::toItemDto)
                .toList();

        return new AdminOrderDto(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getShippingFee(),
                order.getDepositAmount(),
                order.getCreatedAt(),
                order.getConfirmedAt(),
                customer != null ? customer.getFullName() : null,
                customer != null ? customer.getPhone() : null,
                customer != null ? customer.getGovernorate() : null,
                customer != null ? customer.getAddress() : null,
                customer != null ? customer.getNotes() : null,
                itemDtos
        );
    }

    private static AdminOrderItemDto toItemDto(OrderItem item) {
        return new AdminOrderItemDto(
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice()
        );
    }
}