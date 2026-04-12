package com.example.api.dto;

import java.util.List;

public final class RecommendationDtos {
  private RecommendationDtos() {}

  public record RecommendationListRequest(
      String title, String description, String visibility, List<ItemRequest> items) {}

  public record ItemRequest(long bookId, String comment, int displayOrder) {}

  public record RecommendationListResponse(
      long id,
      UserResponse user,
      String title,
      String description,
      String visibility,
      List<ItemResponse> items) {}

  public record ItemResponse(BookDtos.BookResponse book, String comment, int displayOrder) {}
}
