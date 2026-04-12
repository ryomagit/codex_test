package com.example.api.error;

import jakarta.persistence.PersistenceException;
import java.sql.SQLException;

public final class Db {
  private Db() {}

  public static ApiException failure(SQLException exception) {
    if ("23000".equals(exception.getSQLState())) {
      return ApiException.conflict("A record with the same unique key already exists.");
    }
    return new ApiException(
        jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR,
        "database_error",
        "A database error occurred.");
  }

  public static ApiException failure(RuntimeException exception) {
    if (exception instanceof ApiException apiException) {
      return apiException;
    }
    if (exception instanceof PersistenceException persistenceException
        && persistenceException.getCause() instanceof SQLException sqlException) {
      return failure(sqlException);
    }
    return new ApiException(
        jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR,
        "database_error",
        "A database error occurred.");
  }
}
