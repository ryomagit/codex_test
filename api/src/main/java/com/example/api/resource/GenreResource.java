package com.example.api.resource;

import com.example.api.service.GenreService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/genres")
@Produces(MediaType.APPLICATION_JSON)
public final class GenreResource {
  private final GenreService genreService;

  @Inject
  public GenreResource(GenreService genreService) {
    this.genreService = genreService;
  }

  @GET
  public Response list() {
    return Response.ok(genreService.list()).build();
  }

  @GET
  @Path("/{genreId}/ranking")
  public Response ranking(@PathParam("genreId") long genreId) {
    return Response.ok(genreService.ranking(genreId)).build();
  }
}
