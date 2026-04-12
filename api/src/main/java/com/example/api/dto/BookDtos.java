package com.example.api.dto;

import java.util.List;

public final class BookDtos {
  private BookDtos() {}

  public record BookRequest(
      String googleVolumeId,
      String title,
      List<String> authors,
      String description,
      String publisher,
      String publishedDate,
      String isbn10,
      String isbn13,
      String thumbnailUrl,
      List<Long> genreIds,
      List<String> tags) {}

  public record BookResponse(
      long id,
      String googleVolumeId,
      String title,
      List<String> authors,
      String description,
      String publisher,
      String publishedDate,
      String isbn10,
      String isbn13,
      String thumbnailUrl,
      List<GenreResponse> genres,
      List<TagResponse> tags) {}

  public record GoogleBookResponse(
      String googleVolumeId,
      String title,
      List<String> authors,
      String description,
      String publisher,
      String publishedDate,
      String isbn10,
      String isbn13,
      String thumbnailUrl) {}

  public record GenreResponse(long id, String name, String slug) {}

  public record TagResponse(long id, String name, String slug) {}

  public record RankingBookResponse(
      BookResponse book, long favoriteCount, double averageRating, long reviewCount) {}
}
