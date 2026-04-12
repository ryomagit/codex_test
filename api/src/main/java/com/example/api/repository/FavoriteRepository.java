package com.example.api.repository;

import com.example.api.entity.FavoriteEntity;
import com.example.api.entity.FavoriteId;
import com.example.api.entity.QFavoriteEntity;
import com.example.api.model.BookRecord;
import com.example.api.persistence.JpaExecutor;
import com.google.inject.Inject;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;

public final class FavoriteRepository {
  private final JpaExecutor jpa;
  private final BookRepository bookRepository;

  @Inject
  public FavoriteRepository(JpaExecutor jpa, BookRepository bookRepository) {
    this.jpa = jpa;
    this.bookRepository = bookRepository;
  }

  public void add(long userId, long bookId) {
    jpa.writeVoid(entityManager -> entityManager.persist(new FavoriteEntity(userId, bookId)));
  }

  public void remove(long userId, long bookId) {
    jpa.writeVoid(
        entityManager -> {
          FavoriteEntity favorite =
              entityManager.find(FavoriteEntity.class, new FavoriteId(userId, bookId));
          if (favorite != null) {
            entityManager.remove(favorite);
          }
        });
  }

  public List<BookRecord> list(long userId) {
    return jpa.read(
        entityManager -> {
          QFavoriteEntity favorite = QFavoriteEntity.favoriteEntity;
          return new JPAQueryFactory(entityManager)
                  .select(favorite.bookId)
                  .from(favorite)
                  .where(favorite.userId.eq(userId))
                  .orderBy(favorite.createdAt.desc())
                  .fetch()
                  .stream()
                  .map(bookRepository::findById)
                  .flatMap(java.util.Optional::stream)
                  .toList();
        });
  }
}
