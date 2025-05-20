package com.example.delivery.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomCache<K, V> {

    private final Map<K, V> cache;
    private final ScheduledExecutorService executor;
    private final long maxAgeInMillis;
    private final int maxSize;


    public CustomCache() {
        this.maxAgeInMillis = 60000; // 60 секунд
        this.maxSize = 1000;
        this.cache = new LinkedHashMap<K, V>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                boolean shouldRemove = size() > maxSize;
                return shouldRemove;
            }
        };
        this.executor = Executors.newScheduledThreadPool(1);
    }

    public synchronized void put(K key, V value) {
        cache.put(key, value);

        executor.schedule(() -> {
            remove(key);
        }, maxAgeInMillis, TimeUnit.MILLISECONDS);
    }

    public synchronized V get(K key) {
        V value = cache.get(key);
        return value;
    }

    public synchronized void remove(K key) {
        V value = cache.remove(key);
        if (value != null) {}
    }

    public synchronized void clear() {
        cache.clear();
    }

    public synchronized int size() {
        return cache.size();
    }

    public void shutdown() {
        executor.shutdown();
    }
}
