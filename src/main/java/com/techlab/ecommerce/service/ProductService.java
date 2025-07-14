package com.techlab.ecommerce.service;

import com.techlab.ecommerce.dto.ProductDTO;
import com.techlab.ecommerce.exception.ProductNotFoundException;
import com.techlab.ecommerce.model.entity.Category;
import com.techlab.ecommerce.model.entity.Product;
import com.techlab.ecommerce.repository.CategoryRepository;
import com.techlab.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import com.techlab.ecommerce.util.StringUtils;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductDTO::new)
                .toList();
    }

    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(ProductDTO::new)
                .orElseThrow(() -> new ProductNotFoundException());
    }

    public List<ProductDTO> getProductByName(String name) {
        List<Product> productos = productRepository.findByNameContainingIgnoreCase(name);
        if (productos.isEmpty()) {
            throw new ProductNotFoundException();
        }
        return productos.stream().map(ProductDTO::new).toList();
    }

    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = productDTO.toEntity(); // aún no tiene categoría asignada
        product.setName(StringUtils.toTitleCase(product.getName()));

        // Buscar y asignar la categoría
        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseGet(() -> categoryRepository.findByNameIgnoreCase("General")
                            .orElseGet(
                                    () -> categoryRepository.save(new Category("General", "Categoría por defecto"))));
            product.setCategory(category);
        } else {
            Category generalCategory = categoryRepository.findByNameIgnoreCase("General")
                    .orElseGet(() -> categoryRepository.save(new Category("General", "Categoría por defecto")));
            product.setCategory(generalCategory);
        }

        Product savedProduct = productRepository.save(product);
        return new ProductDTO(savedProduct);
    }

    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new);

        existingProduct.setName(StringUtils.toTitleCase(productDTO.getName()));
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setImageUrl(productDTO.getImageUrl());

        // Actualizar la categoría (esta es la parte que faltaba)
        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseGet(() -> categoryRepository.findByNameIgnoreCase("General")
                            .orElseGet(
                                    () -> categoryRepository.save(new Category("General", "Categoría por defecto"))));
            existingProduct.setCategory(category);
        } else {
            // Si no se proporciona categoryId, asignar categoría "General"
            Category generalCategory = categoryRepository.findByNameIgnoreCase("General")
                    .orElseGet(() -> categoryRepository.save(new Category("General", "Categoría por defecto")));
            existingProduct.setCategory(generalCategory);
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return new ProductDTO(updatedProduct);
    }

    public void deleteProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException());
        productRepository.delete(product);
    }

    public Product getEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new);
    }

}
