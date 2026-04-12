package com.example.api.entity;

import java.io.Serializable;
import java.util.Objects;

public class RecommendationListItemId implements Serializable {
  private Long listId;
  private Long bookId;

  public RecommendationListItemId() {}

  public RecommendationListItemId(Long listId, Long bookId) {
    this.listId = listId;
    this.bookId = bookId;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof RecommendationListItemId other)) {
      return false;
    }
    return Objects.equals(listId, other.listId) && Objects.equals(bookId, other.bookId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(listId, bookId);
  }
}
