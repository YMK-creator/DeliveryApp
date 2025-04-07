package com.example.delivery.service.impl;

import com.example.delivery.exception.EntityNotFoundException;
import com.example.delivery.model.Food;
import com.example.delivery.model.Ingredient;
import com.example.delivery.repository.FoodRepository;
import com.example.delivery.repository.IngredientRepository;
import com.example.delivery.service.IngredientService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Transactional
@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {
    private final IngredientRepository ingredientRepository;
    private final FoodRepository foodRepository;

    @Autowired
    public IngredientServiceImpl(
            IngredientRepository ingredientRepository, FoodRepository foodRepository) {
        this.ingredientRepository = ingredientRepository;
        this.foodRepository = foodRepository;
    }

    @Override
    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    @Override
    public Ingredient getIngredientById(Long id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingredient", id));
    }

    @Override
    public Ingredient saveIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    @Override
    public Ingredient updateIngredient(Long id, Ingredient updatedIngredient) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingredient", id));
        ingredient.setName(updatedIngredient.getName());
        return ingredientRepository.save(ingredient);
    }

    @Override
    public void deleteIngredient(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingredient", id));

        // Убираем связи ингредиента с блюдами
        for (Food food : ingredient.getFoods()) {
            food.getIngredients().remove(ingredient);
        }

        // Сохраняем изменения перед удалением ингредиента
        foodRepository.saveAll(ingredient.getFoods());

        // Теперь можно удалить сам ингредиент
        ingredientRepository.delete(ingredient);
    }
}
