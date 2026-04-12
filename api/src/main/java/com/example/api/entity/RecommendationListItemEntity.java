package com.example.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@IdClass(RecommendationListItemId.class)
@Table(name = "recommendation_list_items")
public class RecommendationListItemEntity {
  @Id
  @Column(name = "list_id")
  private Long listId;

  @Id
  @Column(name = "book_id")
  private Long bookId;

  private String comment;

  @Column(name = "display_order", nullable = false)
  private Integer displayOrder;

  public RecommendationListItemEntity() {}

  public RecommendationListItemEntity(
      Long listId, Long bookId, String comment, Integer displayOrder) {
    this.listId = listId;
    this.bookId = bookId;
    this.comment = comment;
    this.displayOrder = displayOrder;
  }

  public Long getBookId() {
    return bookId;
  }

  public String getComment() {
    return comment;
  }

  public Integer getDisplayOrder() {
    return displayOrder;
  }
}
