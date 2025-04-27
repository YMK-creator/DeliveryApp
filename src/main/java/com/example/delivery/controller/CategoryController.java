package com.example.delivery.controller;

import com.example.delivery.model.Category;
import com.example.delivery.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/category")
@Tag(name = "Category Controller", description = "API для управления категориями блюд")
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Получить все категории",
            description = "Возвращает список всех доступных категорий блюд.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список категорий успешно возвращён"),
        @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @Operation(summary = "Получить категорию по ID",
            description = "Возвращает информацию о категории по заданному ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Категория найдена"),
        @ApiResponse(responseCode = "404", description = "Категория не найдена")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(
            @Parameter(description = "ID категории") @PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Создать новую категорию",
            description = "Создаёт новую категорию с переданными данными.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Категория успешно создана"),
        @ApiResponse(responseCode = "400", description = "Неверные данные для создания категории")
    })
    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryService.saveCategory(category);
    }

    @Operation(summary = "Обновить категорию",
            description = "Обновляет информацию о категории по заданному ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Категория успешно обновлена"),
        @ApiResponse(responseCode = "404", description = "Категория не найдена")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @Parameter(description = "ID категории") @PathVariable Long id,
            @RequestBody Category updatedCategory) {
        Category category = categoryService.updateCategory(id, updatedCategory);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Удалить категорию",
            description = "Удаляет категорию по заданному ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Категория успешно удалена"),
        @ApiResponse(responseCode = "404", description = "Категория не найдена")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "ID категории") @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}