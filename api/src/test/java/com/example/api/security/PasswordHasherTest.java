package com.example.api.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class PasswordHasherTest {
  @Test
  void hashesAndVerifiesPassword() {
    PasswordHasher passwordHasher = new PasswordHasher();

    PasswordHasher.PasswordHash hash = passwordHasher.hash("correct horse battery staple");

    assertTrue(passwordHasher.matches("correct horse battery staple", hash.hash(), hash.salt()));
    assertFalse(passwordHasher.matches("wrong password", hash.hash(), hash.salt()));
  }
}
