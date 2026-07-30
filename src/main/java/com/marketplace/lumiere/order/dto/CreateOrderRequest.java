package com.marketplace.lumiere.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record CreateOrderRequest(
        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^01[0-2,5]{1}[0-9]{8}$", message = "Invalid Egyptian phone number")
        String phone,

        @NotBlank(message = "Governorate is required")
        String governorate,

        @NotBlank(message = "Address is required")
        String address,

        String notes, // optional — no rule

        @NotEmpty(message = "Order must have at least one item")
        @Valid
        List<OrderItemRequest> items
) {
    public record OrderItemRequest(
            @NotNull(message = "Product id is required")
            Long productId,

            @NotNull @Min(value = 1, message = "Quantity must be at least 1")
            Integer quantity
    ) {
    }
}