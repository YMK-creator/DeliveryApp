package com.example.delivery.service;

import com.example.delivery.model.Food;
import java.util.List;
import java.util.Optional;

public interface FoodService {
    public List<Food> getFoodByQueryParam(String name, String price);

    public Optional<Food> getFood(String foodId);
}
