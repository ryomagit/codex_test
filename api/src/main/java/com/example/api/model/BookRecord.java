package com.example.api.model;

import com.example.api.dto.BookDtos.GenreResponse;
import com.example.api.dto.BookDtos.TagResponse;
import java.util.List;

public record BookRecord(
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
