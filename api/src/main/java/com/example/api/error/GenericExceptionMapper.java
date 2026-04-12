package com.example.api.error;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class GenericExceptionMapper implements ExceptionMapper<Exception> {
  @Override
  public Response toResponse(Exception exception) {
    if (exception instanceof NotFoundException) {
      return Response.status(Response.Status.NOT_FOUND)
          .type(MediaType.APPLICATION_JSON)
          .entity(ErrorResponse.of("not_found", "Resource was not found."))
          .build();
    }
    if (exception instanceof WebApplicationException webApplicationException) {
      return Response.status(webApplicationException.getResponse().getStatus())
          .type(MediaType.APPLICATION_JSON)
          .entity(ErrorResponse.of("http_error", "Request could not be completed."))
          .build();
    }
    return Response.serverError()
        .type(MediaType.APPLICATION_JSON)
        .entity(ErrorResponse.of("internal_server_error", "An unexpected error occurred."))
        .build();
  }
}
