package com.example.delivery.service.impl;

import com.example.delivery.exception.DuplicateEntityException;
import com.example.delivery.exception.EntityNotFoundException;
import com.example.delivery.exception.InvalidEntityException;
import com.example.delivery.model.Category;
import com.example.delivery.model.Food;
import com.example.delivery.model.Ingredient;
import com.example.delivery.repository.FoodRepository;
import com.example.delivery.repository.IngredientRepository;
import com.example.delivery.utils.CustomCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class FoodServiceImplTest {

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private CustomCache<Long, Food> foodCache;

    @InjectMocks
    private FoodServiceImpl foodService;

    private Food testFood;
    private Ingredient testIngredient;

    @BeforeEach
    void setup() {
        testIngredient = new Ingredient();
        testIngredient.setId(1L);
        testIngredient.setFoods(new ArrayList<>());

        testFood = new Food();
        testFood.setId(1L);
        testFood.setName("Pizza");
        testFood.setIngredients(new ArrayList<>(List.of(testIngredient)));
    }

    @Test
    void getFoodById_shouldReturnCachedFood() {
        when(foodCache.get(1L)).thenReturn(testFood);

        Food result = foodService.getFoodById(1L);

        assertThat(result).isEqualTo(testFood);
        verify(foodRepository, never()).findById(any());
    }

    @Test
    void getFoodById_shouldThrowIfNotFound() {
        when(foodCache.get(1L)).thenReturn(null);
        when(foodRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> foodService.getFoodById(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void saveFood_shouldThrowIfNameIsEmpty() {
        testFood.setName("  ");

        assertThatThrownBy(() -> foodService.saveFood(testFood))
                .isInstanceOf(InvalidEntityException.class);
    }

    @Test
    void saveFood_shouldThrowIfDuplicate() {
        when(foodRepository.findByNameIgnoreCase("Pizza")).thenReturn(Optional.of(new Food()));

        assertThatThrownBy(() -> foodService.saveFood(testFood))
                .isInstanceOf(DuplicateEntityException.class);
    }

    @Test
    void saveFood_shouldThrowIfIngredientsIsNull() {
        testFood.setIngredients(null);

        when(foodRepository.findByNameIgnoreCase("Pizza")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> foodService.saveFood(testFood))
                .isInstanceOf(NullPointerException.class); // or custom exception if preferred
    }

    @Test
    void saveFood_shouldSaveSuccessfully() {
        when(foodRepository.findByNameIgnoreCase("Pizza")).thenReturn(Optional.empty());
        when(ingredientRepository.findAllById(List.of(1L))).thenReturn(List.of(testIngredient));
        when(foodRepository.save(any())).thenReturn(testFood);
        when(ingredientRepository.saveAll(any())).thenReturn(List.of(testIngredient));

        Food result = foodService.saveFood(testFood);

        assertThat(result).isEqualTo(testFood);
        verify(foodCache).put(testFood.getId(), testFood);
    }

    @Test
    void deleteFood_shouldThrowIfNotExists() {
        when(foodRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> foodService.deleteFood(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteFood_shouldDeleteAndEvictFromCache() {
        when(foodRepository.existsById(1L)).thenReturn(true);

        foodService.deleteFood(1L);

        verify(foodRepository).deleteById(1L);
        verify(foodCache).remove(1L);
    }

    @Test
    void addIngredientToFood_shouldAddAndSave() {
        when(foodRepository.findById(1L)).thenReturn(Optional.of(testFood));
        when(ingredientRepository.findById(2L)).thenReturn(Optional.of(new Ingredient(2L, "Cheese", new ArrayList<>())));
        when(foodRepository.save(any())).thenReturn(testFood);

        Food result = foodService.addIngredientToFood(1L, 2L);

        assertThat(result.getIngredients()).hasSize(2);
        verify(foodCache).put(1L, result);
    }

    @Test
    void addIngredientToFood_shouldThrowIfIngredientNotFound() {
        when(foodRepository.findById(1L)).thenReturn(Optional.of(testFood));
        when(ingredientRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> foodService.addIngredientToFood(1L, 2L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateFood_shouldUpdateFields() {
        Food updated = new Food();
        updated.setName("Updated Pizza");
        updated.setPrice(BigDecimal.valueOf(9.99));

        Category newCategory = new Category();
        newCategory.setId(1L);
        newCategory.setName("Fast Food");
        updated.setCategory(newCategory);

        when(foodRepository.findById(1L)).thenReturn(Optional.of(testFood));
        when(foodRepository.save(any())).thenReturn(testFood);

        Food result = foodService.updateFood(1L, updated);

        assertThat(result.getName()).isEqualTo("Updated Pizza");
        assertThat(result.getPrice()).isEqualByComparingTo("9.99");
        assertThat(result.getCategory()).isEqualTo(newCategory);
        verify(foodCache).put(1L, result);
    }

    @Test
    void updateFood_shouldHandleNullFields() {
        Food empty = new Food(); // Все поля null

        when(foodRepository.findById(1L)).thenReturn(Optional.of(testFood));
        when(foodRepository.save(any())).thenReturn(testFood);

        Food result = foodService.updateFood(1L, empty);

        assertThat(result).isEqualTo(testFood);
        verify(foodCache).put(1L, result);
    }

    @Test
    void getAllFood_shouldReturnAll() {
        when(foodRepository.findAll()).thenReturn(List.of(testFood));

        List<Food> result = foodService.getAllFood();

        assertThat(result).containsExactly(testFood);
    }

    @Test
    void searchFoodByName_shouldReturnMatchingFoods() {
        when(foodRepository.findByNameContainingIgnoreCase("piz")).thenReturn(List.of(testFood));

        List<Food> result = foodService.searchFoodByName("piz");

        assertThat(result).containsExactly(testFood);
    }

    @Test
    void searchFoodByCategoryName_shouldReturnMatchingFoods() {
        when(foodRepository.findByCategoryNameContainingIgnoreCase("fast")).thenReturn(List.of(testFood));

        List<Food> result = foodService.searchFoodByCategoryName("fast");

        assertThat(result).containsExactly(testFood);
    }

    @Test
    void saveFoodsBulk_shouldThrowIfNameEmpty() {
        Food badFood = new Food();
        badFood.setName("  ");
        badFood.setIngredients(List.of(testIngredient));

        assertThatThrownBy(() -> foodService.saveFoodsBulk(List.of(badFood)))
                .isInstanceOf(InvalidEntityException.class);
    }

    @Test
    void saveFoodsBulk_shouldThrowIfNameNull() {
        Food badFood = new Food();
        badFood.setName(null);
        badFood.setIngredients(List.of(testIngredient));

        assertThatThrownBy(() -> foodService.saveFoodsBulk(List.of(badFood)))
                .isInstanceOf(InvalidEntityException.class);
    }

    @Test
    void saveFoodsBulk_shouldThrowIfDuplicate() {
        when(foodRepository.findByNameIgnoreCase("Pizza")).thenReturn(Optional.of(new Food()));
        testFood.setIngredients(List.of(testIngredient));

        assertThatThrownBy(() -> foodService.saveFoodsBulk(List.of(testFood)))
                .isInstanceOf(DuplicateEntityException.class);
    }

    @Test
    void saveFoodsBulk_shouldThrowIfNoIngredients() {
        testFood.setIngredients(List.of());

        when(foodRepository.findByNameIgnoreCase("Pizza")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> foodService.saveFoodsBulk(List.of(testFood)))
                .isInstanceOf(InvalidEntityException.class);
    }

    @Test
    void saveFoodsBulk_shouldThrowIfIngredientsNull() {
        testFood.setIngredients(null);

        when(foodRepository.findByNameIgnoreCase("Pizza")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> foodService.saveFoodsBulk(List.of(testFood)))
                .isInstanceOf(InvalidEntityException.class);
    }

    @Test
    void saveFoodsBulk_shouldThrowIfIngredientHasNullId() {
        Ingredient nullIdIngredient = new Ingredient();
        nullIdIngredient.setId(null);

        testFood.setIngredients(List.of(nullIdIngredient));

        when(foodRepository.findByNameIgnoreCase("Pizza")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> foodService.saveFoodsBulk(List.of(testFood)))
                .isInstanceOf(InvalidEntityException.class);
    }

    @Test
    void saveFoodsBulk_shouldSaveSuccessfully() {
        when(foodRepository.findByNameIgnoreCase("Pizza")).thenReturn(Optional.empty());
        when(ingredientRepository.findAllById(List.of(1L))).thenReturn(List.of(testIngredient));
        when(foodRepository.save(any())).thenReturn(testFood);
        when(ingredientRepository.saveAll(any())).thenReturn(List.of(testIngredient));

        List<Food> result = foodService.saveFoodsBulk(List.of(testFood));

        assertThat(result).hasSize(1);
        verify(foodCache).put(testFood.getId(), testFood);
    }
}
