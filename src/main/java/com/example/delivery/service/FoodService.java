package com.example.delivery.service;

import com.example.delivery.model.Food;
import java.util.List;
import java.util.Optional;

public interface FoodService {

    List<Food> saveFoodsBulk(List<Food> foods);

    List<Food> getAllFood(); // Получить весь список еды

    Food getFoodById(Long id); // Получить еду по ID

    Food saveFood(Food food); // Добавить/обновить еду

    Food addIngredientToFood(Long foodId, Long ingredientId);

    Food updateFood(Long id, Food updatedFood);

    void deleteFood(Long id); // Удалить еду по ID

    List<Food> searchFoodByName(String name);

    List<Food> searchFoodByCategoryName(String categoryName);
}