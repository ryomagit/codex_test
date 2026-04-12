package com.example.api.service;

import com.example.api.dto.BookDtos.BookResponse;
import com.example.api.repository.BookRepository;
import com.example.api.repository.FavoriteRepository;
import com.example.api.security.CurrentUser;
import com.google.inject.Inject;
import java.util.List;

public final class FavoriteService {
  private final FavoriteRepository favorites;
  private final BookRepository books;

  @Inject
  public FavoriteService(FavoriteRepository favorites, BookRepository books) {
    this.favorites = favorites;
    this.books = books;
  }

  public void add(CurrentUser user, long bookId) {
    books
        .findById(bookId)
        .orElseThrow(() -> com.example.api.error.ApiException.notFound("Book was not found."));
    favorites.add(user.id(), bookId);
  }

  public void remove(CurrentUser user, long bookId) {
    favorites.remove(user.id(), bookId);
  }

  public List<BookResponse> list(CurrentUser user) {
    return favorites.list(user.id()).stream().map(Responses::book).toList();
  }
}
