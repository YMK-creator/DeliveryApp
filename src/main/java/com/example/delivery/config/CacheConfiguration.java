package com.example.delivery.config;

import com.example.delivery.utils.InMemoryCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfiguration {

    @Bean
    public InMemoryCache<Long, Object> foodCache() {
        return new InMemoryCache<>();
    }
}
