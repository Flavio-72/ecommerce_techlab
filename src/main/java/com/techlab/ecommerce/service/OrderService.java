package com.techlab.ecommerce.service;

import com.techlab.ecommerce.dto.CartItemDTO;
import com.techlab.ecommerce.model.entity.AppUser;
import com.techlab.ecommerce.model.entity.OrderEntity;
import com.techlab.ecommerce.model.entity.OrderItem;
import com.techlab.ecommerce.model.entity.Product;
import com.techlab.ecommerce.model.enums.OrderStatus;
import com.techlab.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartService cartService;
    private final ProductService productService;
    private final OrderRepository orderRepository;
    private final AuthService authService;

    public OrderEntity confirmOrder() {
        AppUser currentUser = authService.getCurrentUser();
        List<CartItemDTO> cartItems = cartService.getCartItems();

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("El carrito está vacío");
        }

        OrderEntity order = new OrderEntity();
        order.setUser(currentUser);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CONFIRMED);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItemDTO item : cartItems) {
            Product product = productService.getEntityById(item.getProductId());

            if (product.getStock() < item.getQuantity()) {
                throw new IllegalStateException("Stock insuficiente para el producto: " + product.getName());
            }

            // Descontar stock
            product.setStock(product.getStock() - item.getQuantity());

            // Crear el ítem de orden
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setOrder(order);
            order.getItems().add(orderItem);

            // Calcular subtotal y agregarlo al total
            BigDecimal price = product.getPrice();
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
            BigDecimal itemTotal = price.multiply(quantity);

            totalAmount = totalAmount.add(itemTotal);
        }

        order.setTotalAmount(totalAmount);

        orderRepository.save(order);
        cartService.clearCart();

        return order;
    }

    public List<OrderEntity> getOrdersForCurrentUser() {
        AppUser currentUser = authService.getCurrentUser();
        return orderRepository.findAll().stream()
                .filter(order -> order.getUser().getId().equals(currentUser.getId()))
                .toList();
    }
}
