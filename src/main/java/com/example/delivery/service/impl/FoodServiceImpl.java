package com.example.delivery.service.impl;

import com.example.delivery.exception.DuplicateEntityException;
import com.example.delivery.exception.EntityNotFoundException;
import com.example.delivery.exception.InvalidEntityException;
import com.example.delivery.model.Food;
import com.example.delivery.model.Ingredient;
import com.example.delivery.repository.FoodRepository;
import com.example.delivery.repository.IngredientRepository;
import com.example.delivery.service.FoodService;
import com.example.delivery.utils.CustomCache;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FoodServiceImpl implements FoodService {
    private final FoodRepository foodRepository;
    private final IngredientRepository ingredientRepository;
    private final CustomCache<Long, Food> foodCache;

    @Autowired
    public FoodServiceImpl(
            FoodRepository foodRepository,
            IngredientRepository ingredientRepository,
            CustomCache<Long, Food> foodCache) {
        this.foodRepository = foodRepository;
        this.ingredientRepository = ingredientRepository;
        this.foodCache = foodCache;
    }

    @Override
    public List<Food> getAllFood() {
        return foodRepository.findAll();
    }

    @Override
    public Food getFoodById(Long id) {
        Food cached = foodCache.get(id);
        if (cached != null) {
            return cached;
        }

        // Если не найдено — выбрасываем исключение
        return foodRepository.findById(id)
                .map(food -> {
                    foodCache.put(id, food);
                    return food;
                })
                .orElseThrow(() -> new EntityNotFoundException("Food", id));
    }

    @Override
    public List<Food> saveFoodsBulk(List<Food> foods) {
        return foods.stream()
                .peek(food -> {
                    if (food.getName() == null || food.getName().trim().isEmpty()) {
                        throw new InvalidEntityException("Food name must not be empty");
                    }

                    boolean exists = foodRepository.findByNameIgnoreCase(food.getName()).isPresent();
                    if (exists) {
                        throw new DuplicateEntityException("Food", "name", food.getName());
                    }

                    if (food.getIngredients() == null || food.getIngredients().isEmpty()) {
                        throw new InvalidEntityException("Food must have at least one ingredient");
                    }

                    boolean hasNullIds = food.getIngredients().stream().anyMatch(i -> i.getId() == null);
                    if (hasNullIds) {
                        throw new InvalidEntityException("Each ingredient must have an ID");
                    }
                })
                .map(food -> {
                    List<Long> ingredientIds = food.getIngredients().stream()
                            .map(Ingredient::getId)
                            .toList();

                    List<Ingredient> existingIngredients = ingredientRepository.findAllById(ingredientIds);
                    food.setIngredients(existingIngredients);

                    Food savedFood = foodRepository.save(food);

                    if (savedFood.getIngredients() != null && !savedFood.getIngredients().isEmpty()) {
                        savedFood.getIngredients().forEach(ingredient -> {
                            if (!ingredient.getFoods().contains(savedFood)) {
                                ingredient.getFoods().add(savedFood);
                            }
                        });
                        ingredientRepository.saveAll(savedFood.getIngredients());
                    }

                    foodCache.put(savedFood.getId(), savedFood);
                    return savedFood;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Food saveFood(Food food) {
        if (food.getName() == null || food.getName().trim().isEmpty()) {
            throw new InvalidEntityException("Food name must not be empty");
        }

        // Проверка на дубликат по имени
        boolean exists = foodRepository.findByNameIgnoreCase(food.getName()).isPresent();
        if (exists) {
            throw new DuplicateEntityException("Food", "name", food.getName());
        }

        List<Long> ingredientIds = food.getIngredients()
                .stream()
                .map(Ingredient::getId)
                .toList();
        List<Ingredient> existingIngredients = ingredientRepository.findAllById(ingredientIds);
        food.setIngredients(existingIngredients);
        Food savedFood = foodRepository.save(food);

        if (savedFood.getIngredients() != null && !savedFood.getIngredients().isEmpty()) {
            savedFood.getIngredients().forEach(ingredient -> {
                if (!ingredient.getFoods().contains(savedFood)) {
                    ingredient.getFoods().add(savedFood);
                }
            });
            ingredientRepository.saveAll(savedFood.getIngredients());
        }

        foodCache.put(savedFood.getId(), savedFood);
        return savedFood;
    }

    @Override
    public Food updateFood(Long id, Food updatedFood) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Food", id));

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

        Food updated = foodRepository.save(food);
        foodCache.put(id, updated);
        return updated;
    }

    @Override
    public Food addIngredientToFood(Long foodId, Long ingredientId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new EntityNotFoundException("Food", foodId));
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new EntityNotFoundException("Ingredient", ingredientId));

        food.getIngredients().add(ingredient);
        Food updatedFood = foodRepository.save(food);
        foodCache.put(foodId, updatedFood);
        return updatedFood;
    }

    @Override
    public void deleteFood(Long id) {
        if (!foodRepository.existsById(id)) {
            throw new EntityNotFoundException("Food", id);
        }
        foodRepository.deleteById(id);
        foodCache.remove(id);
    }

    @Override
    public List<Food> searchFoodByName(String name) {
        return foodRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Food> searchFoodByCategoryName(String categoryName) {
        return foodRepository.findByCategoryNameContainingIgnoreCase(categoryName);
    }
}
