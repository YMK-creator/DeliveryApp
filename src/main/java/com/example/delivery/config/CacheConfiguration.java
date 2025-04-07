package com.example.delivery.config;

import com.example.delivery.model.Food;
import com.example.delivery.utils.CustomCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfiguration {

    @Bean
    public CustomCache<Long, Food> foodCache() {
        return new CustomCache<>();
    }
}
