package com.example.api.security;

import com.example.api.error.ApiException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordHasher {
  private static final SecureRandom RANDOM = new SecureRandom();
  private static final int SALT_BYTES = 16;
  private static final int ITERATIONS = 210_000;
  private static final int KEY_BITS = 256;

  public PasswordHash hash(String password) {
    byte[] salt = new byte[SALT_BYTES];
    RANDOM.nextBytes(salt);
    return hash(password, Base64.getEncoder().encodeToString(salt));
  }

  public PasswordHash hash(String password, String salt) {
    try {
      KeySpec spec =
          new PBEKeySpec(
              password.toCharArray(), Base64.getDecoder().decode(salt), ITERATIONS, KEY_BITS);
      byte[] encoded =
          SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
      return new PasswordHash(Base64.getEncoder().encodeToString(encoded), salt);
    } catch (NoSuchAlgorithmException
        | InvalidKeySpecException
        | IllegalArgumentException exception) {
      throw ApiException.badRequest("Password could not be hashed.");
    }
  }

  public boolean matches(String password, String expectedHash, String salt) {
    return hash(password, salt).hash().equals(expectedHash);
  }

  public record PasswordHash(String hash, String salt) {}
}
