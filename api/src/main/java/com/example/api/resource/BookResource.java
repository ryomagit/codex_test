package com.example.api.resource;

import com.example.api.dto.BookDtos.BookRequest;
import com.example.api.dto.ReviewDtos.ReviewRequest;
import com.example.api.service.AuthService;
import com.example.api.service.BookService;
import com.example.api.service.FavoriteService;
import com.example.api.service.ReviewService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class BookResource {
  private final AuthService authService;
  private final BookService bookService;
  private final FavoriteService favoriteService;
  private final ReviewService reviewService;

  @Inject
  public BookResource(
      AuthService authService,
      BookService bookService,
      FavoriteService favoriteService,
      ReviewService reviewService) {
    this.authService = authService;
    this.bookService = bookService;
    this.favoriteService = favoriteService;
    this.reviewService = reviewService;
  }

  @GET
  @Path("/search")
  public Response search(@QueryParam("q") String query) {
    return Response.ok(bookService.search(query)).build();
  }

  @POST
  public Response save(BookRequest request) {
    return Response.status(Response.Status.CREATED).entity(bookService.save(request)).build();
  }

  @GET
  @Path("/{bookId}")
  public Response get(@PathParam("bookId") long bookId) {
    return Response.ok(bookService.get(bookId)).build();
  }

  @POST
  @Path("/{bookId}/favorite")
  public Response favorite(@Context HttpHeaders headers, @PathParam("bookId") long bookId) {
    favoriteService.add(authService.requireUser(headers), bookId);
    return Response.noContent().build();
  }

  @DELETE
  @Path("/{bookId}/favorite")
  public Response unfavorite(@Context HttpHeaders headers, @PathParam("bookId") long bookId) {
    favoriteService.remove(authService.requireUser(headers), bookId);
    return Response.noContent().build();
  }

  @PUT
  @Path("/{bookId}/review")
  public Response review(
      @Context HttpHeaders headers, @PathParam("bookId") long bookId, ReviewRequest request) {
    reviewService.upsert(authService.requireUser(headers), bookId, request);
    return Response.noContent().build();
  }

  @DELETE
  @Path("/{bookId}/review")
  public Response deleteReview(@Context HttpHeaders headers, @PathParam("bookId") long bookId) {
    reviewService.delete(authService.requireUser(headers), bookId);
    return Response.noContent().build();
  }

  @GET
  @Path("/{bookId}/reviews")
  public Response reviews(@PathParam("bookId") long bookId) {
    return Response.ok(reviewService.byBook(bookId)).build();
  }
}
