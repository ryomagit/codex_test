package com.example.api.service;

import com.example.api.error.ApiException;
import java.util.List;
import java.util.regex.Pattern;

public final class Validation {
  private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

  private Validation() {}

  static String required(String value, String name) {
    if (value == null || value.isBlank()) {
      throw ApiException.badRequest(name + " is required.");
    }
    return value.trim();
  }

  static String email(String value) {
    String email = required(value, "email").toLowerCase();
    if (!EMAIL.matcher(email).matches()) {
      throw ApiException.badRequest("email is invalid.");
    }
    return email;
  }

  static String password(String value) {
    if (value == null || value.length() < 8) {
      throw ApiException.badRequest("password must be at least 8 characters.");
    }
    return value;
  }

  static int rating(int value) {
    if (value < 1 || value > 5) {
      throw ApiException.badRequest("rating must be between 1 and 5.");
    }
    return value;
  }

  static String visibility(String value) {
    String visibility = value == null || value.isBlank() ? "public" : value.trim().toLowerCase();
    if (!List.of("public", "private").contains(visibility)) {
      throw ApiException.badRequest("visibility must be public or private.");
    }
    return visibility;
  }
}
