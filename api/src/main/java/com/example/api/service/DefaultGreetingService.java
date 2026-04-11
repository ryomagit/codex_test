package com.example.api.service;

public final class DefaultGreetingService implements GreetingService {
    @Override
    public String message() {
        return "Hello from Java API";
    }
}

