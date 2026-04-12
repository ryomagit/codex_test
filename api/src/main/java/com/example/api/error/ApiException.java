package com.example.api.error;

import jakarta.ws.rs.core.Response;

public final class ApiException extends RuntimeException {
  private final Response.Status status;
  private final String code;

  public ApiException(Response.Status status, String code, String message) {
    super(message);
    this.status = status;
    this.code = code;
  }

  public Response.Status status() {
    return status;
  }

  public String code() {
    return code;
  }

  public static ApiException badRequest(String message) {
    return new ApiException(Response.Status.BAD_REQUEST, "bad_request", message);
  }

  public static ApiException unauthorized() {
    return new ApiException(
        Response.Status.UNAUTHORIZED, "unauthorized", "Authentication is required.");
  }

  public static ApiException forbidden(String message) {
    return new ApiException(Response.Status.FORBIDDEN, "forbidden", message);
  }

  public static ApiException notFound(String message) {
    return new ApiException(Response.Status.NOT_FOUND, "not_found", message);
  }

  public static ApiException conflict(String message) {
    return new ApiException(Response.Status.CONFLICT, "conflict", message);
  }
}
