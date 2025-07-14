package com.techlab.ecommerce.service;

import com.techlab.ecommerce.dto.CategoryDTO;
import com.techlab.ecommerce.model.entity.Category;
import com.techlab.ecommerce.repository.CategoryRepository;
import com.techlab.ecommerce.util.StringUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(cat -> new CategoryDTO(cat.getId(), cat.getName(), cat.getDescription()))
                .collect(Collectors.toList());
    }

    public CategoryDTO createCategory(CategoryDTO dto) {
        Category category = new Category();
        category.setName(StringUtils.toTitleCase(dto.getName()));
        category.setDescription(dto.getDescription());
        Category savedCategory = categoryRepository.save(category);

        return new CategoryDTO(savedCategory.getId(), savedCategory.getName(), savedCategory.getDescription());
    }

    public Optional<Category> getByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name);
    }

    public CategoryDTO updateCategory(CategoryDTO dto) {
        Category existing = categoryRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + dto.getId()));

        String titleCaseName = StringUtils.toTitleCase(dto.getName());
        existing.setName(titleCaseName);
        existing.setDescription(dto.getDescription());

        Category updated = categoryRepository.save(existing);

        return new CategoryDTO(updated.getId(), updated.getName(), updated.getDescription());
    }

    public Category getOrCreateGeneralCategory() {
        return categoryRepository.findByNameIgnoreCase("General")
                .orElseGet(() -> {
                    Category general = new Category();
                    general.setName("General");
                    general.setDescription("Productos sin categoría específica");
                    return categoryRepository.save(general);
                });
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("La categoría con ID " + id + " no existe.");
        }
        categoryRepository.deleteById(id);
    }

}
