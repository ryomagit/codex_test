package com.example.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.sql.Timestamp;

@Entity
@IdClass(FavoriteId.class)
@Table(name = "favorites")
public class FavoriteEntity {
  @Id
  @Column(name = "user_id")
  private Long userId;

  @Id
  @Column(name = "book_id")
  private Long bookId;

  @Column(name = "created_at", insertable = false, updatable = false)
  private Timestamp createdAt;

  public FavoriteEntity() {}

  public FavoriteEntity(Long userId, Long bookId) {
    this.userId = userId;
    this.bookId = bookId;
  }

  public Long getBookId() {
    return bookId;
  }
}
