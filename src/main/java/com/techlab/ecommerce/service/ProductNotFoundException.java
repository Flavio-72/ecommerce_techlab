package com.techlab.ecommerce.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException() {
        super("Producto no encontrado");
    }

    public ProductNotFoundException(Long productId) {
        super("Producto con ID " + productId + " no existe");
    }
}