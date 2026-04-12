package com.example.api.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

final class SessionTokensTest {
  @Test
  void createsDistinctTokensAndStableHashes() {
    SessionTokens sessionTokens = new SessionTokens();

    String first = sessionTokens.createToken();
    String second = sessionTokens.createToken();

    assertNotEquals(first, second);
    assertEquals(sessionTokens.hash(first), sessionTokens.hash(first));
    assertNotEquals(first, sessionTokens.hash(first));
  }
}
