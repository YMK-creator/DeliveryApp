package com.example.delivery.controller;

import com.example.delivery.model.Food;
import com.example.delivery.service.FoodService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/food")
@Tag(name = "Food Controller", description = "API для управления блюдами")
public class FoodController {

    private final FoodService foodService;

    @Autowired
    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @Operation(summary = "Получить все блюда",
            description = "Возвращает список всех доступных блюд.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список блюд успешно возвращён"),
        @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping
    public List<Food> getAllFood() {
        return foodService.getAllFood();
    }

    @Operation(summary = "Получить блюдо по ID",
            description = "Возвращает информацию о блюде по заданному ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Блюдо найдено"),
        @ApiResponse(responseCode = "404", description = "Блюдо не найдено")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Food> getFoodById(
            @Parameter(description = "ID блюда") @PathVariable Long id) {
        Food food = foodService.getFoodById(id);
        return ResponseEntity.ok(food);
    }

    @Operation(summary = "Создать новое блюдо",
            description = "Создаёт новое блюдо с переданными данными.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Блюдо успешно создано"),
        @ApiResponse(responseCode = "400", description = "Неверные данные для создания блюда")
    })
    @PostMapping
    public Food createFood(@RequestBody Food food) {
        return foodService.saveFood(food);
    }

    @Operation(summary = "Добавить ингредиент к блюду",
            description = "Добавляет ингредиент к заданному блюду по его ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ингредиент успешно добавлен"),
        @ApiResponse(responseCode = "404", description = "Блюдо или ингредиент не найден")
    })
    @PostMapping("/{foodId}/ingredient/{ingredientId}")
    public ResponseEntity<Food> addIngredientToFood(
            @Parameter(description = "ID блюда") @PathVariable Long foodId,
            @Parameter(description = "ID ингредиента") @PathVariable Long ingredientId) {
        Food updatedFood = foodService.addIngredientToFood(foodId, ingredientId);
        return ResponseEntity.ok(updatedFood);
    }

    @Operation(summary = "Обновить блюдо",
            description = "Обновляет информацию о блюде по заданному ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Блюдо успешно обновлено"),
        @ApiResponse(responseCode = "404", description = "Блюдо не найдено")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Food> updateFood(
            @Parameter(description = "ID блюда") @PathVariable Long id,
                                           @RequestBody Food updatedFood) {
        Food food = foodService.updateFood(id, updatedFood);
        return ResponseEntity.ok(food);
    }

    @Operation(summary = "Удалить блюдо", description = "Удаляет блюдо по заданному ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Блюдо успешно удалено"),
        @ApiResponse(responseCode = "404", description = "Блюдо не найдено")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(
            @Parameter(description = "ID блюда") @PathVariable Long id) {
        foodService.deleteFood(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Поиск блюда по имени",
            description = "Ищет блюда по имени, используя поисковый запрос.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Результаты поиска найдены"),
        @ApiResponse(responseCode = "400", description = "Неверный запрос")
    })
    @GetMapping("/search")
    public List<Food> searchFoodByName(@Parameter(
            description = "Поисковый запрос для поиска блюд") @RequestParam String query) {
        return foodService.searchFoodByName(query);
    }
}
