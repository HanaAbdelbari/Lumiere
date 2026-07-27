package com.marketplace.lumiere.order;

// The order lifecycle. Stored as text in the DB (@Enumerated(EnumType.STRING)),
// matching the CHECK constraint on the orders table.
public enum OrderStatus {
    PENDING_DEPOSIT,        // order created, waiting for the customer to pay the deposit
    DEPOSIT_UNDER_REVIEW,   // customer sent the screenshot, admin is reviewing
    CONFIRMED,              // deposit verified
    PREPARING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    DEPOSIT_REJECTED
}