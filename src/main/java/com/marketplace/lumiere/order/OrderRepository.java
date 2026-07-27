package com.marketplace.lumiere.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    // Used to generate the next order number (count of orders this year).
    long count();
}