package com.example.api;

final class JsonResponses {
    private JsonResponses() {
    }

    static String health() {
        return "{\"status\":\"ok\"}";
    }

    static String hello() {
        return "{\"message\":\"Hello from Java API\"}";
    }
}

