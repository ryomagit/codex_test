package com.example.api.entity;

import java.io.Serializable;
import java.util.Objects;

public class FavoriteId implements Serializable {
  private Long userId;
  private Long bookId;

  public FavoriteId() {}

  public FavoriteId(Long userId, Long bookId) {
    this.userId = userId;
    this.bookId = bookId;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof FavoriteId other)) {
      return false;
    }
    return Objects.equals(userId, other.userId) && Objects.equals(bookId, other.bookId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, bookId);
  }
}
