package com.marketplace.lumiere.product;

import com.marketplace.lumiere.product.dto.ProductDetailDto;
import com.marketplace.lumiere.product.dto.ProductSummaryDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private static final int NEW_ARRIVALS_LIMIT = 8;

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Shop: all active products.
    public List<ProductSummaryDto> getAllProducts() {
        return productRepository
                .findByIsActiveTrueOrderByDisplayOrderAscCreatedAtDesc()
                .stream()
                .map(ProductSummaryDto::from)
                .toList();
    }

    // Category page: active products in one category.
    public List<ProductSummaryDto> getProductsByCategory(String categorySlug) {
        return productRepository
                .findByCategorySlugAndIsActiveTrueOrderByDisplayOrderAsc(categorySlug)
                .stream()
                .map(ProductSummaryDto::from)
                .toList();
    }

    // Home — New Arrivals: newest few. The limit lives here in the service,
    // so the repository query stays general and reusable.
    public List<ProductSummaryDto> getNewArrivals() {
        return productRepository
                .findByIsActiveTrueOrderByCreatedAtDesc()
                .stream()
                .limit(NEW_ARRIVALS_LIMIT)
                .map(ProductSummaryDto::from)
                .toList();
    }

    // Home Offers section + Sale page: products actually on sale.
    public List<ProductSummaryDto> getOnSaleProducts() {
        return productRepository.findOnSale()
                .stream()
                .map(ProductSummaryDto::from)
                .toList();
    }

    // Product details page. Throws if not found or inactive.
    public ProductDetailDto getProductBySlug(String slug) {
        Product product = productRepository
                .findBySlugAndIsActiveTrue(slug)
                .orElseThrow(() -> new ProductNotFoundException(slug));
        return ProductDetailDto.from(product);
    }
}