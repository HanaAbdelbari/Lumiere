package com.marketplace.lumiere.product;

import com.marketplace.lumiere.category.Category;
import com.marketplace.lumiere.category.CategoryRepository;
import com.marketplace.lumiere.product.dto.AdminProductDto;
import com.marketplace.lumiere.product.dto.AdminProductDetailDto;
import com.marketplace.lumiere.product.dto.ProductRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public AdminProductService(ProductRepository productRepository,
                               CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminProductDto> getAllProducts() {
        return productRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(AdminProductDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminProductDetailDto getProductDetails(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
        return AdminProductDetailDto.from(product);
    }

    public AdminProductDto createProduct(ProductRequest request) {
        Product product = new Product();
        applyRequest(product, request);
        Product saved = productRepository.save(product);
        return AdminProductDto.from(saved);
    }

    public AdminProductDto updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
        applyRequest(product, request);
        return AdminProductDto.from(product);
    }

    // Soft delete — hide the product instead of removing it (keeps order history intact).
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
        product.setIsActive(false);
    }

    // Restore a hidden product — show it in the store again.
    public void restoreProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
        product.setIsActive(true);
    }

    // Copy fields from the request onto the product (used by create & update).
    private void applyRequest(Product product, ProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Category not found: " + request.categoryId()));

        product.setCategory(category);
        product.setName(request.name());
        product.setSlug(request.slug());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setSalePrice(request.salePrice());
        product.setMaterial(request.material());
        product.setSize(request.size());
        product.setChainLength(request.chainLength());
        product.setStockQuantity(request.stockQuantity() != null ? request.stockQuantity() : 0);
        product.setDisplayOrder(request.displayOrder() != null ? request.displayOrder() : 0);
        if (product.getIsActive() == null) {
            product.setIsActive(true);
        }

        // Replace the images with the new list of URLs.
        product.getImages().clear();
        if (request.imageUrls() != null) {
            int order = 0;
            for (String url : request.imageUrls()) {
                if (url == null || url.isBlank()) continue;
                ProductImage image = new ProductImage();
                image.setImageUrl(url.trim());
                image.setDisplayOrder(order++);
                image.setProduct(product);
                product.getImages().add(image);
            }
        }
    }
}