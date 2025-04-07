package com.example.delivery.repository;

import com.example.delivery.model.Food;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface FoodRepository extends JpaRepository<Food, Long> {
    @Query("SELECT f FROM Food f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Food> findByNameContainingIgnoreCase(@Param("name") String name);

    Optional<Food> findByNameIgnoreCase(String name);
}

