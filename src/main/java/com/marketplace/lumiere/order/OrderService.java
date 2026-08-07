package com.marketplace.lumiere.order;

import com.marketplace.lumiere.order.dto.CreateOrderRequest;
import com.marketplace.lumiere.order.dto.OrderResponse;
import com.marketplace.lumiere.order.dto.AdminOrderDto;
import com.marketplace.lumiere.product.Product;
import com.marketplace.lumiere.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.time.LocalDateTime;
import java.util.List;

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
        order.setStatus(OrderStatus.PENDING_DEPOSIT);

        BigDecimal productsTotal = BigDecimal.ZERO;

        for (CreateOrderRequest.OrderItemRequest line : request.items()) {
            Product product = productRepository.findById(line.productId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Product not found: " + line.productId()));

            if (line.quantity() == null || line.quantity() < 1) {
                throw new IllegalArgumentException("Invalid quantity for product " + line.productId());
            }

            BigDecimal unitPrice = product.getEffectivePrice();

            OrderItem item = new OrderItem(product, line.quantity(), unitPrice);
            order.addItem(item);

            productsTotal = productsTotal.add(
                    unitPrice.multiply(BigDecimal.valueOf(line.quantity()))
            );
        }

        BigDecimal shipping = shippingCalculator.calculate(productsTotal, request.governorate());
        BigDecimal total = productsTotal.add(shipping);

        // ⚡ التعديل هنا: تقريب الديبوزيت لأقرب 5 لأسفل بدون كسور ⚡
        double rawHalf = total.doubleValue() * 0.5;
        double roundedDeposit = Math.floor(rawHalf / 5.0) * 5.0;
        BigDecimal deposit = BigDecimal.valueOf(roundedDeposit);

        order.setShippingFee(shipping);
        order.setTotalAmount(total);
        order.setDepositAmount(deposit);
        order.setOrderNumber(generateOrderNumber());

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

    // ===== Admin =====

    @Transactional(readOnly = true)
    public List<AdminOrderDto> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(AdminOrderDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminOrderDto getOrderDetails(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
        return AdminOrderDto.from(order);
    }

    @Transactional
    public AdminOrderDto updateStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));

        OrderStatus previousStatus = order.getStatus();

        if (newStatus == OrderStatus.CONFIRMED && previousStatus != OrderStatus.CONFIRMED) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                int currentStock = product.getStockQuantity();
                int requestedQuantity = item.getQuantity();

                if (currentStock < requestedQuantity) {
                    throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
                }

                product.setStockQuantity(currentStock - requestedQuantity);
                productRepository.saveAndFlush(product);
            }

            if (order.getConfirmedAt() == null) {
                order.setConfirmedAt(LocalDateTime.now());
            }
        }

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.saveAndFlush(order);
        return AdminOrderDto.from(updatedOrder);
    }

    private String generateOrderNumber() {
        long next = orderRepository.count() + 1;
        return String.format("LUM-%d%05d", Year.now().getValue(), next);
    }
}