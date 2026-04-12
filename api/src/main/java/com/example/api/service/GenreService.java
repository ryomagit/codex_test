package com.example.api.service;

import com.example.api.dto.BookDtos.GenreResponse;
import com.example.api.dto.BookDtos.RankingBookResponse;
import com.example.api.repository.BookRepository;
import com.example.api.repository.GenreRepository;
import com.google.inject.Inject;
import java.util.List;

public final class GenreService {
  private final GenreRepository genres;
  private final BookRepository books;

  @Inject
  public GenreService(GenreRepository genres, BookRepository books) {
    this.genres = genres;
    this.books = books;
  }

  public List<GenreResponse> list() {
    return genres.list();
  }

  public List<RankingBookResponse> ranking(long genreId) {
    return books.rankingByGenre(genreId);
  }
}
