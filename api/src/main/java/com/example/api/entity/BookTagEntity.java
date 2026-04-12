package com.example.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@IdClass(BookTagId.class)
@Table(name = "book_tags")
public class BookTagEntity {
  @Id
  @Column(name = "book_id")
  private Long bookId;

  @Id
  @Column(name = "tag_id")
  private Long tagId;

  public BookTagEntity() {}

  public BookTagEntity(Long bookId, Long tagId) {
    this.bookId = bookId;
    this.tagId = tagId;
  }
}
