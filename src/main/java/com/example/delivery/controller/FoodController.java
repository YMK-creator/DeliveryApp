package com.example.delivery.controller;

import com.example.delivery.model.Food;
import com.example.delivery.service.impl.InMemoryFoodServiceImpl;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/food/")
public class FoodController {

    private final InMemoryFoodServiceImpl foodService;

    public FoodController(InMemoryFoodServiceImpl foodService) {
        this.foodService = foodService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Food> getFoodById(@PathVariable("id") String id) {
        Optional<Food> food = foodService.getFood(id);
        if (food.isPresent()) {
            return ResponseEntity.ok(food.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<Food> getAllFood(@RequestParam(name = "name", required = false) String name,
                                 @RequestParam(name = "price", required = false) String price) {
        return foodService.getFoodByQueryParam(name, price);
    }
}
