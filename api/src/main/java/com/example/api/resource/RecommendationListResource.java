package com.example.api.resource;

import com.example.api.security.CurrentUser;
import com.example.api.service.AuthService;
import com.example.api.service.RecommendationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/recommendation-lists")
@Produces(MediaType.APPLICATION_JSON)
public final class RecommendationListResource {
  private final AuthService authService;
  private final RecommendationService recommendationService;

  @Inject
  public RecommendationListResource(
      AuthService authService, RecommendationService recommendationService) {
    this.authService = authService;
    this.recommendationService = recommendationService;
  }

  @GET
  @Path("/{listId}")
  public Response get(@Context HttpHeaders headers, @PathParam("listId") long listId) {
    CurrentUser viewer = authService.authenticate(headers).orElse(null);
    return Response.ok(recommendationService.get(viewer, listId)).build();
  }
}
