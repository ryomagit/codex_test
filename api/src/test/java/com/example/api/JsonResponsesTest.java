package com.example.api;

public final class JsonResponsesTest {
    private JsonResponsesTest() {
    }

    public static void main(String[] args) {
        assertEquals("{\"status\":\"ok\"}", JsonResponses.health());
        assertEquals("{\"message\":\"Hello from Java API\"}", JsonResponses.hello());
        System.out.println("JsonResponsesTest passed");
    }

    private static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }
}

