package com.techlab.ecommerce.controller;

import com.techlab.ecommerce.dto.CartItemDTO;
import com.techlab.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v0.1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public void addToCart(@RequestParam Long productId, @RequestParam int quantity) {
        cartService.addToCart(productId, quantity);
    }

    @GetMapping
    public List<CartItemDTO> getCartItems() {
        return cartService.getCartItems();
    }
}
