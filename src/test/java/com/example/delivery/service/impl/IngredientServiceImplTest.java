package com.example.delivery.service.impl;

import com.example.delivery.exception.EntityNotFoundException;
import com.example.delivery.model.Food;
import com.example.delivery.model.Ingredient;
import com.example.delivery.repository.FoodRepository;
import com.example.delivery.repository.IngredientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class IngredientServiceImplTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private FoodRepository foodRepository;

    @InjectMocks
    private IngredientServiceImpl ingredientService;

    private Ingredient testIngredient;

    @BeforeEach
    void setUp() {
        testIngredient = new Ingredient();
        testIngredient.setId(1L);
        testIngredient.setName("Tomato");
    }

    @Test
    void getAllIngredients_shouldReturnList() {
        when(ingredientRepository.findAll()).thenReturn(List.of(testIngredient));

        List<Ingredient> result = ingredientService.getAllIngredients();

        assertThat(result).containsExactly(testIngredient);
    }

    @Test
    void getIngredientById_shouldReturnIngredient() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(testIngredient));

        Ingredient result = ingredientService.getIngredientById(1L);

        assertThat(result).isEqualTo(testIngredient);
    }

    @Test
    void getIngredientById_shouldThrowIfNotFound() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ingredientService.getIngredientById(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void saveIngredient_shouldSaveSuccessfully() {
        when(ingredientRepository.save(testIngredient)).thenReturn(testIngredient);

        Ingredient result = ingredientService.saveIngredient(testIngredient);

        assertThat(result).isEqualTo(testIngredient);
    }

    @Test
    void updateIngredient_shouldUpdateName() {
        Ingredient updated = new Ingredient();
        updated.setName("Updated");

        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(testIngredient));
        when(ingredientRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Ingredient result = ingredientService.updateIngredient(1L, updated);

        assertThat(result.getName()).isEqualTo("Updated");
    }

    @Test
    void updateIngredient_shouldThrowIfNotFound() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ingredientService.updateIngredient(1L, new Ingredient()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteIngredient_shouldUnlinkFromFoodsAndDelete() {
        Food food = new Food();
        food.setId(1L);
        food.setIngredients(new ArrayList<>(List.of(testIngredient)));

        testIngredient.setFoods(new ArrayList<>(List.of(food)));

        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(testIngredient));

        ingredientService.deleteIngredient(1L);

        assertThat(food.getIngredients()).doesNotContain(testIngredient);
        verify(foodRepository).saveAll(List.of(food));
        verify(ingredientRepository).delete(testIngredient);
    }

    @Test
    void deleteIngredient_shouldThrowIfNotFound() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ingredientService.deleteIngredient(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
