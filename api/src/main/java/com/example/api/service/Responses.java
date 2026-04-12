package com.example.api.service;

import com.example.api.dto.BookDtos;
import com.example.api.dto.RecommendationDtos;
import com.example.api.dto.ReviewDtos;
import com.example.api.dto.UserResponse;
import com.example.api.model.BookRecord;
import com.example.api.model.RecommendationItemRecord;
import com.example.api.model.RecommendationListRecord;
import com.example.api.model.ReviewRecord;
import com.example.api.model.UserRecord;

final class Responses {
  private Responses() {}

  static UserResponse user(UserRecord user) {
    return new UserResponse(
        user.id(),
        user.email(),
        user.displayName(),
        user.bio(),
        user.avatarUrl(),
        user.headline(),
        user.followerCount());
  }

  static BookDtos.BookResponse book(BookRecord book) {
    return new BookDtos.BookResponse(
        book.id(),
        book.googleVolumeId(),
        book.title(),
        book.authors(),
        book.description(),
        book.publisher(),
        book.publishedDate(),
        book.isbn10(),
        book.isbn13(),
        book.thumbnailUrl(),
        book.genres(),
        book.tags());
  }

  static ReviewDtos.ReviewResponse review(ReviewRecord review) {
    return new ReviewDtos.ReviewResponse(
        review.id(),
        review.bookId(),
        user(review.user()),
        review.rating(),
        review.body(),
        review.createdAt(),
        review.updatedAt());
  }

  static RecommendationDtos.RecommendationListResponse recommendation(
      RecommendationListRecord list) {
    return new RecommendationDtos.RecommendationListResponse(
        list.id(),
        user(list.user()),
        list.title(),
        list.description(),
        list.visibility(),
        list.items().stream().map(Responses::item).toList());
  }

  private static RecommendationDtos.ItemResponse item(RecommendationItemRecord item) {
    return new RecommendationDtos.ItemResponse(
        book(item.book()), item.comment(), item.displayOrder());
  }
}
