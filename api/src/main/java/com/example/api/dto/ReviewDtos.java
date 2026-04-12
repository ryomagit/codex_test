package com.example.api.dto;

public final class ReviewDtos {
  private ReviewDtos() {}

  public record ReviewRequest(int rating, String body) {}

  public record ReviewResponse(
      long id,
      long bookId,
      UserResponse user,
      int rating,
      String body,
      String createdAt,
      String updatedAt) {}
}
