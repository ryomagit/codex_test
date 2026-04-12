package com.example.api.service;

import com.example.api.dto.ReviewDtos.ReviewRequest;
import com.example.api.dto.ReviewDtos.ReviewResponse;
import com.example.api.error.ApiException;
import com.example.api.repository.BookRepository;
import com.example.api.repository.ReviewRepository;
import com.example.api.security.CurrentUser;
import com.google.inject.Inject;
import java.util.List;

public final class ReviewService {
  private final ReviewRepository reviews;
  private final BookRepository books;

  @Inject
  public ReviewService(ReviewRepository reviews, BookRepository books) {
    this.reviews = reviews;
    this.books = books;
  }

  public void upsert(CurrentUser user, long bookId, ReviewRequest request) {
    if (request == null) {
      throw ApiException.badRequest("Request body is required.");
    }
    books.findById(bookId).orElseThrow(() -> ApiException.notFound("Book was not found."));
    reviews.upsert(user.id(), bookId, Validation.rating(request.rating()), request.body());
  }

  public void delete(CurrentUser user, long bookId) {
    reviews.delete(user.id(), bookId);
  }

  public List<ReviewResponse> byBook(long bookId) {
    books.findById(bookId).orElseThrow(() -> ApiException.notFound("Book was not found."));
    return reviews.byBook(bookId).stream().map(Responses::review).toList();
  }
}
