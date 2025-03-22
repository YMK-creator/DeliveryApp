package com.example.delivery.service.impl;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CacheServiceImpl<T> {
    private final Map<String, T> cache = new HashMap<>();

    public Optional<T> get(String key) {
        return Optional.ofNullable(cache.get(key));
    }

    public void put(String key, T value) {
        cache.put(key, value);
    }

    public void clear() {
        cache.clear();
    }

    public void remove(String key) {
        cache.remove(key);
    }
}
