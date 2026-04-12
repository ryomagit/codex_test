package com.example.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@IdClass(BookGenreId.class)
@Table(name = "book_genres")
public class BookGenreEntity {
  @Id
  @Column(name = "book_id")
  private Long bookId;

  @Id
  @Column(name = "genre_id")
  private Long genreId;

  public BookGenreEntity() {}

  public BookGenreEntity(Long bookId, Long genreId) {
    this.bookId = bookId;
    this.genreId = genreId;
  }

  public Long getGenreId() {
    return genreId;
  }
}
