package com.example.delivery.controller;

import com.example.delivery.model.Food;
import com.example.delivery.service.FoodService;
//import com.example.delivery.service.impl.InMemoryFoodServiceImpl;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/food")
public class FoodController {
    private final FoodService foodService;

    @Autowired
    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping
    public List<Food> getAllFood() {
        return foodService.getAllFood();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Food> getFoodById(@PathVariable Long id) {
        Optional<Food> food = foodService.getFoodById(id);
        return food.map(ResponseEntity::ok) // Если найдено -> вернуть 200 OK
                .orElseGet(() -> ResponseEntity.notFound().build()); // Если нет -> вернуть 404
    }

    @PostMapping
    public Food createFood(@RequestBody Food food) {
        return foodService.saveFood(food);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Food> updateFood(@PathVariable Long id, @RequestBody Food updatedFood) {
        Food food = foodService.updateFood(id, updatedFood);
        return ResponseEntity.ok(food);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable Long id) {
        foodService.deleteFood(id);
        return ResponseEntity.noContent().build();
    }
}

