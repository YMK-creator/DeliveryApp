package com.example.delivery.service.impl;

import com.example.delivery.exception.EntityNotFoundException;
import com.example.delivery.model.Category;
import com.example.delivery.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        category = new Category();
        category.setId(1L);
        category.setName("Fast Food");
    }

    @Test
    void getAllCategories_shouldReturnAll() {
        List<Category> categories = List.of(category);
        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();

        assertThat(result).containsExactly(category);
    }

    @Test
    void getCategoryById_shouldReturnIfExists() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryById(1L);

        assertThat(result).isEqualTo(category);
    }

    @Test
    void getCategoryById_shouldThrowIfNotExists() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Category");
    }

    @Test
    void saveCategory_shouldSaveSuccessfully() {
        when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoryService.saveCategory(category);

        assertThat(result).isEqualTo(category);
    }

    @Test
    void deleteCategory_shouldThrowIfNotExists() {
        when(categoryRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> categoryService.deleteCategory(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteCategory_shouldDeleteIfExists() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        categoryService.deleteCategory(1L);

        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void updateCategory_shouldUpdateName() {
        Category updated = new Category();
        updated.setName("Drinks");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any())).thenReturn(category);

        Category result = categoryService.updateCategory(1L, updated);

        assertThat(result.getName()).isEqualTo("Drinks");
        verify(categoryRepository).save(category);
    }
}
