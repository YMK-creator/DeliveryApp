package com.example.delivery.service.impl;

import com.example.delivery.model.Food;
import com.example.delivery.service.FoodService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Реализация сервиса для управления едой в памяти.
 */
@Service
public class InMemoryFoodServiceImpl implements FoodService {

    @Override
    public List<Food> getFoodByQueryParam(String name, String price) {
        List<Food> foodList = new ArrayList<>();
        // Создаём 10 разных объектов еды
        foodList.add(new Food("1", "Pizza", "500"));
        foodList.add(new Food("2", "Burger", "300"));
        foodList.add(new Food("3", "Sushi", "800"));
        foodList.add(new Food("4", "Pizza", "600")); // Дублируем название
        foodList.add(new Food("5", "Steak", "1200"));
        foodList.add(new Food("6", "Burger", "300")); // Дублируем название и цену
        foodList.add(new Food("7", "Salad", "200"));
        foodList.add(new Food("8", "Soup", "250"));
        foodList.add(new Food("9", "Fish", "700"));
        foodList.add(new Food("10", "Fish", "700")); // Дублируем название и цену

        return foodList.stream()
                .filter(food -> (name == null || food.getName().equalsIgnoreCase(name)))
                .filter(food -> (price == null
                        || Integer.parseInt(food.getPrice()) >= Integer.parseInt(price)))
                .toList();
    }

    @Override
    public Optional<Food> getFood(String foodId) {
        return Optional.of(new Food(String.valueOf(foodId), "name", "300"));
    }
}
