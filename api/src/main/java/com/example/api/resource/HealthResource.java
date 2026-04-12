package com.example.api.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/health")
public final class HealthResource {
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getHealth() {
    return Response.ok("{\"status\":\"ok\"}", MediaType.APPLICATION_JSON).build();
  }
}
