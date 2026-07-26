package com.marketplace.lumiere.product;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String slug) {
        super("Product not found: " + slug);
    }
}