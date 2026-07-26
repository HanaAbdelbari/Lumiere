package com.marketplace.lumiere.product;

import com.marketplace.lumiere.product.dto.ProductDetailDto;
import com.marketplace.lumiere.product.dto.ProductSummaryDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Shop page — all active products.
    @GetMapping
    public List<ProductSummaryDto> getAllProducts() {
        return productService.getAllProducts();
    }

    // Home — New Arrivals.
    // Fixed path kept ABOVE /{slug} and distinct from it so it is never
    // mistaken for a product slug.
    @GetMapping("/new-arrivals")
    public List<ProductSummaryDto> getNewArrivals() {
        return productService.getNewArrivals();
    }

    // Home Offers section + Sale page.
    @GetMapping("/on-sale")
    public List<ProductSummaryDto> getOnSale() {
        return productService.getOnSaleProducts();
    }

    // Category page — products in one category.
    @GetMapping("/category/{categorySlug}")
    public List<ProductSummaryDto> getByCategory(@PathVariable String categorySlug) {
        return productService.getProductsByCategory(categorySlug);
    }

    // Product details page — single product by slug.
    // Declared last: fixed paths above win, so this only matches real slugs.
    @GetMapping("/{slug}")
    public ProductDetailDto getBySlug(@PathVariable String slug) {
        return productService.getProductBySlug(slug);
    }
}