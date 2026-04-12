package com.example.api.entity;

import java.io.Serializable;
import java.util.Objects;

public class UserFollowId implements Serializable {
  private Long followerUserId;
  private Long followedUserId;

  public UserFollowId() {}

  public UserFollowId(Long followerUserId, Long followedUserId) {
    this.followerUserId = followerUserId;
    this.followedUserId = followedUserId;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof UserFollowId other)) {
      return false;
    }
    return Objects.equals(followerUserId, other.followerUserId)
        && Objects.equals(followedUserId, other.followedUserId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(followerUserId, followedUserId);
  }
}
