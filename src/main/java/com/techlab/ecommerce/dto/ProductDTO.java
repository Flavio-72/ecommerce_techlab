package com.techlab.ecommerce.dto;

import java.math.BigDecimal;

import com.techlab.ecommerce.model.entity.Product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ProductDTO.java
@Data
@AllArgsConstructor
@NoArgsConstructor

public class ProductDTO {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String description;

    private String imageUrl;

    @NotNull(message = "El precio es obligatorio")
    private BigDecimal price;

    @NotNull(message = "El stock es obligatorio")
    private Integer stock;

    private Long categoryId;
    private String categoryName;

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.imageUrl = product.getImageUrl();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.categoryId = product.getCategory() != null ? product.getCategory().getId() : null;
        this.categoryName = product.getCategory() != null ? product.getCategory().getName() : null;
    }

    public Product toEntity() {
        Product product = new Product();
        product.setId(this.id);
        product.setName(this.name);
        product.setDescription(this.description);
        product.setImageUrl(this.imageUrl);
        product.setPrice(this.price);
        product.setStock(this.stock);
        // si tenés categoría, también seteala acá
        return product;
    }

}
