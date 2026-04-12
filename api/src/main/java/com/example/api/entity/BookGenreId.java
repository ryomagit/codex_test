package com.example.api.entity;

import java.io.Serializable;
import java.util.Objects;

public class BookGenreId implements Serializable {
  private Long bookId;
  private Long genreId;

  public BookGenreId() {}

  public BookGenreId(Long bookId, Long genreId) {
    this.bookId = bookId;
    this.genreId = genreId;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof BookGenreId other)) {
      return false;
    }
    return Objects.equals(bookId, other.bookId) && Objects.equals(genreId, other.genreId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookId, genreId);
  }
}
