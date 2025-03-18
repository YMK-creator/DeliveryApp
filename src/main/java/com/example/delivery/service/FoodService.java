package com.example.delivery.service;

import com.example.delivery.model.Food;
import java.util.List;
import java.util.Optional;

public interface FoodService {
    List<Food> getAllFood(); // Получить весь список еды

    Optional<Food> getFoodById(Long id); // Получить еду по ID

    Food saveFood(Food food); // Добавить/обновить еду

    Food addIngredientToFood(Long foodId, Long ingredientId);

    Food updateFood(Long id, Food updatedFood);

    void deleteFood(Long id); // Удалить еду по ID
}