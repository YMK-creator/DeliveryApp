package com.example.delivery.service;

import com.example.delivery.model.Ingredient;
import java.util.List;
import java.util.Optional;

public interface IngredientService {
    List<Ingredient> getAllIngredients();
    Optional<Ingredient> getIngredientById(Long id);
    Ingredient saveIngredient(Ingredient ingredient);
    Ingredient updateIngredient(Long id, Ingredient updatedIngredient);
    void deleteIngredient(Long id);
}
