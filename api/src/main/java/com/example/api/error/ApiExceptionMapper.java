package com.example.api.error;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class ApiExceptionMapper implements ExceptionMapper<ApiException> {
  @Override
  public Response toResponse(ApiException exception) {
    return Response.status(exception.status())
        .type(MediaType.APPLICATION_JSON)
        .entity(ErrorResponse.of(exception.code(), exception.getMessage()))
        .build();
  }
}
