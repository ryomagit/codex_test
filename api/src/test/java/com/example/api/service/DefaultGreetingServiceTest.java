package com.example.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class DefaultGreetingServiceTest {
  @Test
  void returnsMessage() {
    GreetingService service = new DefaultGreetingService();

    assertEquals("Hello from Java API", service.message());
  }
}
