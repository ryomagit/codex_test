package com.example.api.resource;

import com.example.api.dto.RecommendationDtos.RecommendationListRequest;
import com.example.api.service.AuthService;
import com.example.api.service.FavoriteService;
import com.example.api.service.RecommendationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/me")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class MeResource {
  private final AuthService authService;
  private final FavoriteService favoriteService;
  private final RecommendationService recommendationService;

  @Inject
  public MeResource(
      AuthService authService,
      FavoriteService favoriteService,
      RecommendationService recommendationService) {
    this.authService = authService;
    this.favoriteService = favoriteService;
    this.recommendationService = recommendationService;
  }

  @GET
  @Path("/favorites")
  public Response favorites(@Context HttpHeaders headers) {
    return Response.ok(favoriteService.list(authService.requireUser(headers))).build();
  }

  @POST
  @Path("/recommendation-lists")
  public Response createList(@Context HttpHeaders headers, RecommendationListRequest request) {
    return Response.status(Response.Status.CREATED)
        .entity(recommendationService.create(authService.requireUser(headers), request))
        .build();
  }

  @PUT
  @Path("/recommendation-lists/{listId}")
  public Response updateList(
      @Context HttpHeaders headers,
      @PathParam("listId") long listId,
      RecommendationListRequest request) {
    return Response.ok(
            recommendationService.update(authService.requireUser(headers), listId, request))
        .build();
  }

  @DELETE
  @Path("/recommendation-lists/{listId}")
  public Response deleteList(@Context HttpHeaders headers, @PathParam("listId") long listId) {
    recommendationService.delete(authService.requireUser(headers), listId);
    return Response.noContent().build();
  }
}
