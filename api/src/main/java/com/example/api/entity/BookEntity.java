package com.example.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "books")
public class BookEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "google_volume_id", nullable = false, unique = true)
  private String googleVolumeId;

  @Column(nullable = false)
  private String title;

  private String description;
  private String publisher;

  @Column(name = "published_date")
  private String publishedDate;

  @Column(name = "isbn_10")
  private String isbn10;

  @Column(name = "isbn_13")
  private String isbn13;

  @Column(name = "thumbnail_url")
  private String thumbnailUrl;

  @Column(name = "created_at", insertable = false, updatable = false)
  private Timestamp createdAt;

  public Long getId() {
    return id;
  }

  public String getGoogleVolumeId() {
    return googleVolumeId;
  }

  public void setGoogleVolumeId(String googleVolumeId) {
    this.googleVolumeId = googleVolumeId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  public String getPublishedDate() {
    return publishedDate;
  }

  public void setPublishedDate(String publishedDate) {
    this.publishedDate = publishedDate;
  }

  public String getIsbn10() {
    return isbn10;
  }

  public void setIsbn10(String isbn10) {
    this.isbn10 = isbn10;
  }

  public String getIsbn13() {
    return isbn13;
  }

  public void setIsbn13(String isbn13) {
    this.isbn13 = isbn13;
  }

  public String getThumbnailUrl() {
    return thumbnailUrl;
  }

  public void setThumbnailUrl(String thumbnailUrl) {
    this.thumbnailUrl = thumbnailUrl;
  }
}
