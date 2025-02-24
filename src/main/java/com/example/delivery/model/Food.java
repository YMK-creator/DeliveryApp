package com.example.delivery.model;

/**
 * Класс еды.
 */

public class Food {
    private String id;
    private String name;
    private String price;

    /**
     * Конструктор с параметрами.
     */
    public Food(String id, String name, String price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    /**
     * Конструктор без параметров.
     */
    public Food() {
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
