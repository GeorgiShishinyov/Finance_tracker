package com.example.financetracker.model.exceptions;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message){
        super(message);
    }
}
