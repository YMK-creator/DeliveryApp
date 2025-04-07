package com.example.delivery.exception;

public class DuplicateEntityException extends RuntimeException {
    public DuplicateEntityException(String entityName, String fieldName, String fieldValue) {
        super(String.format("%s with %s '%s' already exists", entityName, fieldName, fieldValue));
    }
}
