package com.example.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.api.error.ApiException;
import org.junit.jupiter.api.Test;

final class ValidationTest {
  @Test
  void validatesRatingRange() {
    assertEquals(5, Validation.rating(5));
    assertThrows(ApiException.class, () -> Validation.rating(0));
    assertThrows(ApiException.class, () -> Validation.rating(6));
  }

  @Test
  void normalizesVisibility() {
    assertEquals("public", Validation.visibility(null));
    assertEquals("private", Validation.visibility(" PRIVATE "));
    assertThrows(ApiException.class, () -> Validation.visibility("friends"));
  }
}
