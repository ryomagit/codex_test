package com.example.api.service;

import com.example.api.dto.AuthDtos.AuthResponse;
import com.example.api.dto.AuthDtos.LoginRequest;
import com.example.api.dto.AuthDtos.RegisterRequest;
import com.example.api.error.ApiException;
import com.example.api.model.UserRecord;
import com.example.api.repository.SessionRepository;
import com.example.api.repository.UserRepository;
import com.example.api.security.CurrentUser;
import com.example.api.security.PasswordHasher;
import com.example.api.security.SessionTokens;
import com.google.inject.Inject;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.HttpHeaders;
import java.util.Optional;

public final class AuthService {
  public static final String SESSION_COOKIE = "book_app_session";
  private final UserRepository users;
  private final SessionRepository sessions;
  private final PasswordHasher passwordHasher;
  private final SessionTokens sessionTokens;

  @Inject
  public AuthService(
      UserRepository users,
      SessionRepository sessions,
      PasswordHasher passwordHasher,
      SessionTokens sessionTokens) {
    this.users = users;
    this.sessions = sessions;
    this.passwordHasher = passwordHasher;
    this.sessionTokens = sessionTokens;
  }

  public LoginResult register(RegisterRequest request) {
    if (request == null) {
      throw ApiException.badRequest("Request body is required.");
    }
    PasswordHasher.PasswordHash passwordHash =
        passwordHasher.hash(Validation.password(request.password()));
    UserRecord user =
        users.create(
            Validation.email(request.email()),
            Validation.required(request.displayName(), "displayName"),
            passwordHash.hash(),
            passwordHash.salt());
    return loginResult(user);
  }

  public LoginResult login(LoginRequest request) {
    if (request == null) {
      throw ApiException.badRequest("Request body is required.");
    }
    UserRecord user =
        users
            .findByEmail(Validation.email(request.email()))
            .orElseThrow(ApiException::unauthorized);
    if (!passwordHasher.matches(
        Validation.password(request.password()), user.passwordHash(), user.passwordSalt())) {
      throw ApiException.unauthorized();
    }
    return loginResult(user);
  }

  public AuthResponse me(CurrentUser currentUser) {
    return new AuthResponse(
        Responses.user(users.findById(currentUser.id()).orElseThrow(ApiException::unauthorized)));
  }

  public Optional<CurrentUser> authenticate(HttpHeaders headers) {
    Cookie cookie = headers.getCookies().get(SESSION_COOKIE);
    if (cookie == null || cookie.getValue().isBlank()) {
      return Optional.empty();
    }
    return sessions
        .findUserByTokenHash(sessionTokens.hash(cookie.getValue()))
        .map(user -> new CurrentUser(user.id(), user.email(), user.displayName()));
  }

  public CurrentUser requireUser(HttpHeaders headers) {
    return authenticate(headers).orElseThrow(ApiException::unauthorized);
  }

  public void logout(HttpHeaders headers) {
    Cookie cookie = headers.getCookies().get(SESSION_COOKIE);
    if (cookie != null && !cookie.getValue().isBlank()) {
      sessions.delete(sessionTokens.hash(cookie.getValue()));
    }
  }

  private LoginResult loginResult(UserRecord user) {
    String token = sessionTokens.createToken();
    sessions.create(user.id(), sessionTokens.hash(token));
    return new LoginResult(token, new AuthResponse(Responses.user(user)));
  }

  public record LoginResult(String token, AuthResponse response) {}
}
