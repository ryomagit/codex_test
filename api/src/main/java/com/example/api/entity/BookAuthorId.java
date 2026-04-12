package com.example.api.entity;

import java.io.Serializable;
import java.util.Objects;

public class BookAuthorId implements Serializable {
  private Long bookId;
  private Integer displayOrder;

  public BookAuthorId() {}

  public BookAuthorId(Long bookId, Integer displayOrder) {
    this.bookId = bookId;
    this.displayOrder = displayOrder;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof BookAuthorId other)) {
      return false;
    }
    return Objects.equals(bookId, other.bookId) && Objects.equals(displayOrder, other.displayOrder);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookId, displayOrder);
  }
}
