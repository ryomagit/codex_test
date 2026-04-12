package com.example.api.repository;

import com.example.api.dto.BookDtos.GenreResponse;
import com.example.api.entity.GenreEntity;
import com.example.api.persistence.JpaExecutor;
import com.google.inject.Inject;
import java.util.List;

public final class GenreRepository {
  private final JpaExecutor jpa;

  @Inject
  public GenreRepository(JpaExecutor jpa) {
    this.jpa = jpa;
  }

  public List<GenreResponse> list() {
    return jpa.read(
        entityManager ->
            entityManager
                .createQuery("SELECT g FROM GenreEntity g ORDER BY g.name", GenreEntity.class)
                .getResultStream()
                .map(genre -> new GenreResponse(genre.getId(), genre.getName(), genre.getSlug()))
                .toList());
  }
}
