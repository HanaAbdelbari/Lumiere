package com.marketplace.lumiere.order;

import com.marketplace.lumiere.order.dto.CreateOrderRequest;
import com.marketplace.lumiere.order.dto.OrderResponse;
import com.marketplace.lumiere.product.Product;
import com.marketplace.lumiere.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ShippingCalculator shippingCalculator;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        ShippingCalculator shippingCalculator) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.shippingCalculator = shippingCalculator;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        Order order = new Order();
        order.setStatus(OrderStatus.PENDING_DEPOSIT); // created before payment

        BigDecimal productsTotal = BigDecimal.ZERO;

        // Build each line item. Prices come from the DB, not the request.
        for (CreateOrderRequest.OrderItemRequest line : request.items()) {
            Product product = productRepository.findById(line.productId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Product not found: " + line.productId()));

            if (line.quantity() == null || line.quantity() < 1) {
                throw new IllegalArgumentException("Invalid quantity for product " + line.productId());
            }

            // Effective price = sale price if on sale, else normal price.
            BigDecimal unitPrice = product.getEffectivePrice();

            OrderItem item = new OrderItem(product, line.quantity(), unitPrice);
            order.addItem(item);

            productsTotal = productsTotal.add(
                    unitPrice.multiply(BigDecimal.valueOf(line.quantity()))
            );
        }

        // Shipping from the backend rules.
        BigDecimal shipping = shippingCalculator.calculate(productsTotal, request.governorate());
        BigDecimal total = productsTotal.add(shipping);

        // Deposit = 50% of the final total, rounded to 2 decimals.
        BigDecimal deposit = total.multiply(new BigDecimal("0.5"))
                .setScale(2, RoundingMode.HALF_UP);

        order.setShippingFee(shipping);
        order.setTotalAmount(total);
        order.setDepositAmount(deposit);
        order.setOrderNumber(generateOrderNumber());

        // Customer details.
        CustomerInfo info = new CustomerInfo(
                request.fullName(),
                request.phone(),
                request.governorate(),
                request.address(),
                request.notes()
        );
        order.setCustomerInfo(info);

        Order saved = orderRepository.save(order);
        return OrderResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderNumber));
        return OrderResponse.from(order);
    }

    // Generate a number like LUM-202600001 (year + zero-padded sequence).
    private String generateOrderNumber() {
        long next = orderRepository.count() + 1;
        return String.format("LUM-%d%05d", Year.now().getValue(), next);
    }
}