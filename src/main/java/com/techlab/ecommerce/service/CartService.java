package com.techlab.ecommerce.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.techlab.ecommerce.dto.CartItemDTO;
import com.techlab.ecommerce.dto.ProductDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {
    private final ProductService productService;
    private final Map<Long, CartItemDTO> temporaryCart = new ConcurrentHashMap<>();

    public void addToCart(Long productId, int quantity) {
        ProductDTO product = productService.getProductById(productId);
        BigDecimal price = product.getPrice();

        temporaryCart.compute(productId, (key, existingItem) -> {
            int newQuantity = (existingItem == null ? 0 : existingItem.getQuantity()) + quantity;
            BigDecimal subTotal = price.multiply(BigDecimal.valueOf(newQuantity));

            return new CartItemDTO(
                    productId,
                    product.getName(),
                    newQuantity,
                    price,
                    product.getImageUrl(),
                    subTotal);
        });
    }

    public List<CartItemDTO> getCartItems() {
        return new ArrayList<>(temporaryCart.values());
    }

    public void clearCart() {
        temporaryCart.clear();
    }

}