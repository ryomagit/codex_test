package com.example.api.resource;

import com.example.api.dto.AuthDtos.LoginRequest;
import com.example.api.dto.AuthDtos.RegisterRequest;
import com.example.api.service.AuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class AuthResource {
  private final AuthService authService;

  @Inject
  public AuthResource(AuthService authService) {
    this.authService = authService;
  }

  @POST
  @Path("/register")
  public Response register(RegisterRequest request) {
    AuthService.LoginResult result = authService.register(request);
    return Response.status(Response.Status.CREATED)
        .header(HttpHeaders.SET_COOKIE, cookie(result.token()))
        .entity(result.response())
        .build();
  }

  @POST
  @Path("/login")
  public Response login(LoginRequest request) {
    AuthService.LoginResult result = authService.login(request);
    return Response.ok(result.response())
        .header(HttpHeaders.SET_COOKIE, cookie(result.token()))
        .build();
  }

  @POST
  @Path("/logout")
  public Response logout(@Context HttpHeaders headers) {
    authService.logout(headers);
    return Response.noContent()
        .header(
            HttpHeaders.SET_COOKIE,
            AuthService.SESSION_COOKIE + "=; Max-Age=0; Path=/; HttpOnly; SameSite=Lax")
        .build();
  }

  @GET
  @Path("/me")
  public Response me(@Context HttpHeaders headers) {
    return Response.ok(authService.me(authService.requireUser(headers))).build();
  }

  private String cookie(String token) {
    return AuthService.SESSION_COOKIE
        + "="
        + token
        + "; Max-Age=2592000; Path=/; HttpOnly; SameSite=Lax";
  }
}
