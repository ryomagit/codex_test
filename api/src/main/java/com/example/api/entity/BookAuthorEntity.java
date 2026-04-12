package com.example.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@IdClass(BookAuthorId.class)
@Table(name = "book_authors")
public class BookAuthorEntity {
  @Id
  @Column(name = "book_id")
  private Long bookId;

  @Id
  @Column(name = "display_order")
  private Integer displayOrder;

  @Column(name = "author_name", nullable = false)
  private String authorName;

  public BookAuthorEntity() {}

  public BookAuthorEntity(Long bookId, String authorName, Integer displayOrder) {
    this.bookId = bookId;
    this.authorName = authorName;
    this.displayOrder = displayOrder;
  }

  public String getAuthorName() {
    return authorName;
  }
}
