package com.rest.API.exception;

public class AnimalNotFoundException extends Exception {
    private long animal_id;

    public AnimalNotFoundException(long animal_id) {
        super(String.format("Animal is not found with id : '%s'", animal_id));
    }
}