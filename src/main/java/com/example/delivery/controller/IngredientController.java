package com.example.delivery.controller;

import com.example.delivery.model.Ingredient;
import com.example.delivery.service.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/ingredient")
@Tag(name = "Ingredient Controller", description = "API для управления ингредиентами блюд")
public class IngredientController {
    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @Operation(summary = "Получить все ингредиенты",
            description = "Возвращает список всех доступных ингредиентов.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список ингредиентов успешно возвращён"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping
    public List<Ingredient> getAllIngredients() {
        return ingredientService.getAllIngredients();
    }

    @Operation(summary = "Получить ингредиент по ID",
            description = "Возвращает информацию об ингредиенте по заданному ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ингредиент найден"),
            @ApiResponse(responseCode = "404", description = "Ингредиент не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredientById(
            @Parameter(description = "ID ингредиента") @PathVariable Long id) {
        Ingredient ingredient = ingredientService.getIngredientById(id);
        return ResponseEntity.ok(ingredient);
    }

    @Operation(summary = "Создать новый ингредиент",
            description = "Создаёт новый ингредиент с переданными данными.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ингредиент успешно создан"),
            @ApiResponse(responseCode = "400", description = "Неверные данные для создания ингредиента")
    })
    @PostMapping
    public Ingredient createIngredient(@RequestBody Ingredient ingredient) {
        return ingredientService.saveIngredient(ingredient);
    }

    @Operation(summary = "Обновить ингредиент",
            description = "Обновляет информацию об ингредиенте по заданному ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ингредиент успешно обновлён"),
            @ApiResponse(responseCode = "404", description = "Ингредиент не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Ingredient> updateIngredient(
            @Parameter(description = "ID ингредиента") @PathVariable Long id,
            @RequestBody Ingredient updatedIngredient) {
        Ingredient ingredient = ingredientService.updateIngredient(id, updatedIngredient);
        return ResponseEntity.ok(ingredient);
    }

    @Operation(summary = "Удалить ингредиент",
            description = "Удаляет ингредиент по заданному ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ингредиент успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Ингредиент не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(
            @Parameter(description = "ID ингредиента") @PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }
}