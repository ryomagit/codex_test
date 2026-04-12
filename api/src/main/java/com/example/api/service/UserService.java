package com.example.api.service;

import com.example.api.dto.UserResponse;
import com.example.api.error.ApiException;
import com.example.api.repository.UserRepository;
import com.example.api.security.CurrentUser;
import com.google.inject.Inject;
import java.util.List;

public final class UserService {
  private final UserRepository users;

  @Inject
  public UserService(UserRepository users) {
    this.users = users;
  }

  public UserResponse get(long userId) {
    return Responses.user(
        users.findById(userId).orElseThrow(() -> ApiException.notFound("User was not found.")));
  }

  public List<UserResponse> featured() {
    return users.featured().stream().map(Responses::user).toList();
  }

  public void follow(CurrentUser currentUser, long followedId) {
    users.follow(currentUser.id(), followedId);
  }

  public void unfollow(CurrentUser currentUser, long followedId) {
    users.unfollow(currentUser.id(), followedId);
  }
}
