package com.example.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_profiles")
public class UserProfileEntity {
  @Id
  @Column(name = "user_id")
  private Long userId;

  private String bio;

  @Column(name = "avatar_url")
  private String avatarUrl;

  private String headline;

  public Long getUserId() {
    return userId;
  }

  public String getBio() {
    return bio;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public String getHeadline() {
    return headline;
  }
}
