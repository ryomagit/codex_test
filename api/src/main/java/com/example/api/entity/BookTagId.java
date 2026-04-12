package com.example.api.entity;

import java.io.Serializable;
import java.util.Objects;

public class BookTagId implements Serializable {
  private Long bookId;
  private Long tagId;

  public BookTagId() {}

  public BookTagId(Long bookId, Long tagId) {
    this.bookId = bookId;
    this.tagId = tagId;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof BookTagId other)) {
      return false;
    }
    return Objects.equals(bookId, other.bookId) && Objects.equals(tagId, other.tagId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookId, tagId);
  }
}
