package com.example.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.sql.Timestamp;

@Entity
@IdClass(UserFollowId.class)
@Table(name = "user_follows")
public class UserFollowEntity {
  @Id
  @Column(name = "follower_user_id")
  private Long followerUserId;

  @Id
  @Column(name = "followed_user_id")
  private Long followedUserId;

  @Column(name = "created_at", insertable = false, updatable = false)
  private Timestamp createdAt;

  public UserFollowEntity() {}

  public UserFollowEntity(Long followerUserId, Long followedUserId) {
    this.followerUserId = followerUserId;
    this.followedUserId = followedUserId;
  }

  public Long getFollowerUserId() {
    return followerUserId;
  }

  public Long getFollowedUserId() {
    return followedUserId;
  }
}
