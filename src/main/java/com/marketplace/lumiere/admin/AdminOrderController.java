package com.marketplace.lumiere.admin;

import com.marketplace.lumiere.order.OrderService;
import com.marketplace.lumiere.order.OrderStatus;
import com.marketplace.lumiere.order.dto.AdminOrderDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// All routes here are under /api/admin, so they're protected by JWT
// (see SecurityConfig — /api/admin/** requires ROLE_ADMIN).
@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // List all orders (newest first).
    @GetMapping
    public List<AdminOrderDto> getAll() {
        return orderService.getAllOrders();
    }

    // One order's full details.
    @GetMapping("/{id}")
    public AdminOrderDto getOne(@PathVariable Long id) {
        return orderService.getOrderDetails(id);
    }

    // Change an order's status. Body: { "status": "CONFIRMED" }
    @PatchMapping("/{id}/status")
    public AdminOrderDto updateStatus(@PathVariable Long id,
                                      @RequestBody UpdateStatusRequest request) {
        OrderStatus status = OrderStatus.valueOf(request.status());
        return orderService.updateStatus(id, status);
    }

    public record UpdateStatusRequest(String status) {
    }
}