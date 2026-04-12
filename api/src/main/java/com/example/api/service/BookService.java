package com.example.api.service;

import com.example.api.dto.BookDtos.BookRequest;
import com.example.api.dto.BookDtos.BookResponse;
import com.example.api.dto.BookDtos.GoogleBookResponse;
import com.example.api.error.ApiException;
import com.example.api.external.GoogleBooksClient;
import com.example.api.repository.BookRepository;
import com.google.inject.Inject;
import java.util.List;

public final class BookService {
  private final BookRepository books;
  private final GoogleBooksClient googleBooksClient;

  @Inject
  public BookService(BookRepository books, GoogleBooksClient googleBooksClient) {
    this.books = books;
    this.googleBooksClient = googleBooksClient;
  }

  public List<GoogleBookResponse> search(String query) {
    return googleBooksClient.search(Validation.required(query, "q"));
  }

  public BookResponse save(BookRequest request) {
    if (request == null) {
      throw ApiException.badRequest("Request body is required.");
    }
    Validation.required(request.googleVolumeId(), "googleVolumeId");
    Validation.required(request.title(), "title");
    return Responses.book(books.upsert(request));
  }

  public BookResponse get(long bookId) {
    return Responses.book(
        books.findById(bookId).orElseThrow(() -> ApiException.notFound("Book was not found.")));
  }
}
