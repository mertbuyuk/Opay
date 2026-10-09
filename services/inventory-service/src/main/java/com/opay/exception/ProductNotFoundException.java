package com.opay.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String sku) {
        super("No stock record found for sku: " + sku);
    }
}
