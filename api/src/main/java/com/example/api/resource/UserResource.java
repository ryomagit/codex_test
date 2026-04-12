package com.example.api.resource;

import com.example.api.security.CurrentUser;
import com.example.api.service.AuthService;
import com.example.api.service.RecommendationService;
import com.example.api.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
public final class UserResource {
  private final AuthService authService;
  private final RecommendationService recommendationService;
  private final UserService userService;

  @Inject
  public UserResource(
      AuthService authService,
      RecommendationService recommendationService,
      UserService userService) {
    this.authService = authService;
    this.recommendationService = recommendationService;
    this.userService = userService;
  }

  @GET
  @Path("/{userId}")
  public Response get(@PathParam("userId") long userId) {
    return Response.ok(userService.get(userId)).build();
  }

  @GET
  @Path("/featured")
  public Response featured() {
    return Response.ok(userService.featured()).build();
  }

  @GET
  @Path("/{userId}/recommendation-lists")
  public Response recommendationLists(
      @Context HttpHeaders headers, @PathParam("userId") long userId) {
    userService.get(userId);
    CurrentUser viewer = authService.authenticate(headers).orElse(null);
    return Response.ok(recommendationService.listByUser(viewer, userId)).build();
  }

  @POST
  @Path("/{userId}/follow")
  public Response follow(@Context HttpHeaders headers, @PathParam("userId") long userId) {
    userService.follow(authService.requireUser(headers), userId);
    return Response.noContent().build();
  }

  @DELETE
  @Path("/{userId}/follow")
  public Response unfollow(@Context HttpHeaders headers, @PathParam("userId") long userId) {
    userService.unfollow(authService.requireUser(headers), userId);
    return Response.noContent().build();
  }
}
