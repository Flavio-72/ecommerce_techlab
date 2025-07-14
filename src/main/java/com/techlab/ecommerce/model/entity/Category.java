package com.techlab.ecommerce.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 50)
    @Column(nullable = false, unique = true)
    private String name;

    @Size(max = 255)
    private String description;

    @OneToMany(mappedBy = "category")
    private List<Product> products;

    // Constructor personalizado para uso interno (ej. categoría por defecto)
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
