package com.example.delivery.service;

import java.util.Optional;

public interface CacheService<T> {
    Optional<T> get(String key);
    void put(String key, T value);
    void clear();
    void remove(String key);
}
