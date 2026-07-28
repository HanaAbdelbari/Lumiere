package com.marketplace.lumiere.admin;

import com.marketplace.lumiere.product.AdminProductService;
import com.marketplace.lumiere.product.dto.AdminProductDto;
import com.marketplace.lumiere.product.dto.AdminProductDetailDto;
import com.marketplace.lumiere.product.dto.ProductRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Under /api/admin — protected by JWT (ROLE_ADMIN).
@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final AdminProductService adminProductService;

    public AdminProductController(AdminProductService adminProductService) {
        this.adminProductService = adminProductService;
    }

    // All products (including hidden).
    @GetMapping
    public List<AdminProductDto> getAll() {
        return adminProductService.getAllProducts();
    }

    // One product's full data (for the edit form).
    @GetMapping("/{id}")
    public AdminProductDetailDto getOne(@PathVariable Long id) {
        return adminProductService.getProductDetails(id);
    }

    // Create a product.
    @PostMapping
    public ResponseEntity<AdminProductDto> create(@RequestBody ProductRequest request) {
        AdminProductDto created = adminProductService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Update a product.
    @PutMapping("/{id}")
    public AdminProductDto update(@PathVariable Long id, @RequestBody ProductRequest request) {
        return adminProductService.updateProduct(id, request);
    }

    // Soft-delete (hide) a product.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminProductService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}