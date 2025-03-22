package com.example.delivery.service.impl;

import com.example.delivery.model.Food;
import com.example.delivery.model.Ingredient;
import com.example.delivery.repository.FoodRepository;
import com.example.delivery.repository.IngredientRepository;
import com.example.delivery.service.FoodService;
import java.util.List;
import java.util.Optional;

import com.example.delivery.utils.InMemoryCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FoodServiceImpl implements FoodService {
    private final FoodRepository foodRepository;
    private final IngredientRepository ingredientRepository;
    private final InMemoryCache<Long, Food> foodCache;

    @Autowired
    public FoodServiceImpl(
            FoodRepository foodRepository, IngredientRepository ingredientRepository, InMemoryCache<Long, Food> foodCache) {
        this.foodRepository = foodRepository;
        this.ingredientRepository = ingredientRepository;
        this.foodCache = foodCache;
    }

    @Override
    public List<Food> getAllFood() {
        return foodRepository.findAll();
    }

    @Override
    public Optional<Food> getFoodById(Long id) {
        Food cachedAccount = foodCache.get(id);
        if (cachedAccount != null) {
            return Optional.of(cachedAccount);
        }

        // Если в кэше нет, запрашиваем из БД
        Optional<Food> account = foodRepository.findById(id);
        account.ifPresent(acc -> foodCache.put(id, acc)); // Сохраняем в кэш
        return account;
    }

    @Override
    public Food saveFood(Food food) {
        List<Long> ingredientIds = food
                .getIngredients().stream().map(Ingredient::getId).toList();

        // Загружаем ингредиенты из БД, чтобы избежать проблем с detach-объектами
        List<Ingredient> existingIngredients = ingredientRepository.findAllById(ingredientIds);

        // Явно устанавливаем ингредиенты
        food.setIngredients(existingIngredients);

        // Сохраняем еду в БД
        Food savedFood = foodRepository.save(food);

        // Явно сохраняем связь с ингредиентами, если они есть
        if (savedFood.getIngredients() != null && !savedFood.getIngredients().isEmpty()) {
            savedFood.getIngredients().forEach(ingredient -> {
                if (!ingredient.getFoods().contains(savedFood)) {
                    ingredient.getFoods().add(savedFood);
                }
            });

            ingredientRepository.saveAll(savedFood.getIngredients()); // Явно обновляем ингредиенты
        }

        // Кэшируем
        foodCache.put(savedFood.getId(), savedFood);

        return savedFood;
    }

    @Override
    public Food updateFood(Long id, Food updatedFood) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found"));

        if (updatedFood.getName() != null) {
            food.setName(updatedFood.getName());
        }
        if (updatedFood.getPrice() != null) {
            food.setPrice(updatedFood.getPrice());
        }
        if (updatedFood.getCategory() != null) {
            food.setCategory(updatedFood.getCategory());
        }
        if (updatedFood.getIngredients() != null) {
            List<Long> ingredientIds = updatedFood
                    .getIngredients().stream().map(Ingredient::getId).toList();
            List<Ingredient> existingIngredients = ingredientRepository.findAllById(ingredientIds);
            food.setIngredients(existingIngredients);
        }

        // Сохраняем обновленное блюдо
        Food updated = foodRepository.save(food);

        // Обновляем кэш
        foodCache.put(id, updated);

        return updated;
    }

    @Override
    public Food addIngredientToFood(Long foodId, Long ingredientId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Food not found"));
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));

        food.getIngredients().add(ingredient);
        Food updatedFood = foodRepository.save(food);

        // Обновляем кэш
        foodCache.put(foodId, updatedFood);

        return updatedFood;
    }

    @Override
    public void deleteFood(Long id) {
        foodRepository.deleteById(id);
        foodCache.evict(id); // Удаляем еду из кэша
    }

    @Override
    public List<Food> searchFoodByName(String name) {
        return foodRepository.findByNameContainingIgnoreCase(name);
    }
    
}
