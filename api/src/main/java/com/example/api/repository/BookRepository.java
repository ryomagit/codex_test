package com.example.api.repository;

import com.example.api.dto.BookDtos.BookRequest;
import com.example.api.dto.BookDtos.GenreResponse;
import com.example.api.dto.BookDtos.RankingBookResponse;
import com.example.api.dto.BookDtos.TagResponse;
import com.example.api.entity.BookAuthorEntity;
import com.example.api.entity.BookEntity;
import com.example.api.entity.BookGenreEntity;
import com.example.api.entity.BookTagEntity;
import com.example.api.entity.GenreEntity;
import com.example.api.entity.QBookEntity;
import com.example.api.entity.QBookGenreEntity;
import com.example.api.entity.QFavoriteEntity;
import com.example.api.entity.QReviewEntity;
import com.example.api.entity.TagEntity;
import com.example.api.model.BookRecord;
import com.example.api.persistence.JpaExecutor;
import com.google.inject.Inject;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public final class BookRepository {
  private final JpaExecutor jpa;

  @Inject
  public BookRepository(JpaExecutor jpa) {
    this.jpa = jpa;
  }

  public BookRecord upsert(BookRequest request) {
    return jpa.write(
        entityManager -> {
          BookEntity book =
              entityManager
                  .createQuery(
                      "SELECT b FROM BookEntity b WHERE b.googleVolumeId = :googleVolumeId",
                      BookEntity.class)
                  .setParameter("googleVolumeId", request.googleVolumeId())
                  .getResultStream()
                  .findFirst()
                  .orElseGet(BookEntity::new);
          book.setGoogleVolumeId(request.googleVolumeId());
          book.setTitle(request.title());
          book.setDescription(request.description());
          book.setPublisher(request.publisher());
          book.setPublishedDate(request.publishedDate());
          book.setIsbn10(request.isbn10());
          book.setIsbn13(request.isbn13());
          book.setThumbnailUrl(request.thumbnailUrl());
          if (book.getId() == null) {
            entityManager.persist(book);
          }
          entityManager.flush();
          replaceAuthors(entityManager, book.getId(), request.authors());
          replaceGenres(entityManager, book.getId(), request.genreIds());
          replaceTags(entityManager, book.getId(), request.tags());
          entityManager.flush();
          return map(entityManager, book);
        });
  }

  public Optional<BookRecord> findById(long id) {
    return jpa.read(
        entityManager ->
            Optional.ofNullable(entityManager.find(BookEntity.class, id))
                .map(book -> map(entityManager, book)));
  }

  public List<RankingBookResponse> rankingByGenre(long genreId) {
    return jpa.read(
        entityManager -> {
          QBookEntity book = QBookEntity.bookEntity;
          QBookGenreEntity bookGenre = QBookGenreEntity.bookGenreEntity;
          QFavoriteEntity favorite = QFavoriteEntity.favoriteEntity;
          QReviewEntity review = QReviewEntity.reviewEntity;
          List<Tuple> rows =
              new JPAQueryFactory(entityManager)
                  .select(
                      book.id,
                      favorite.userId.countDistinct(),
                      review.rating.avg(),
                      review.id.countDistinct())
                  .from(book)
                  .join(bookGenre)
                  .on(bookGenre.bookId.eq(book.id))
                  .leftJoin(favorite)
                  .on(favorite.bookId.eq(book.id))
                  .leftJoin(review)
                  .on(review.bookId.eq(book.id))
                  .where(bookGenre.genreId.eq(genreId))
                  .groupBy(book.id)
                  .orderBy(
                      favorite.userId.countDistinct().desc(),
                      review.rating.avg().coalesce(0.0).desc(),
                      review.id.countDistinct().desc(),
                      book.id.asc())
                  .limit(20)
                  .fetch();
          return rows.stream()
              .map(row -> rankingRow(entityManager, row, book, favorite, review))
              .toList();
        });
  }

  private RankingBookResponse rankingRow(
      EntityManager entityManager,
      Tuple row,
      QBookEntity book,
      QFavoriteEntity favorite,
      QReviewEntity review) {
    Long bookId = row.get(book.id);
    Long favoriteCount = row.get(favorite.userId.countDistinct());
    Double averageRating = row.get(review.rating.avg());
    Long reviewCount = row.get(review.id.countDistinct());
    return new RankingBookResponse(
        toResponse(map(entityManager, entityManager.find(BookEntity.class, bookId))),
        favoriteCount == null ? 0 : favoriteCount,
        averageRating == null ? 0 : averageRating,
        reviewCount == null ? 0 : reviewCount);
  }

  public static com.example.api.dto.BookDtos.BookResponse toResponse(BookRecord book) {
    return new com.example.api.dto.BookDtos.BookResponse(
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

  private void replaceAuthors(EntityManager entityManager, long bookId, List<String> authors) {
    entityManager
        .createQuery("DELETE FROM BookAuthorEntity a WHERE a.bookId = :bookId")
        .setParameter("bookId", bookId)
        .executeUpdate();
    if (authors == null) {
      return;
    }
    for (int index = 0; index < authors.size(); index++) {
      String author = authors.get(index);
      if (author != null && !author.isBlank()) {
        entityManager.persist(new BookAuthorEntity(bookId, author.trim(), index));
      }
    }
  }

  private void replaceGenres(EntityManager entityManager, long bookId, List<Long> genreIds) {
    entityManager
        .createQuery("DELETE FROM BookGenreEntity bg WHERE bg.bookId = :bookId")
        .setParameter("bookId", bookId)
        .executeUpdate();
    if (genreIds == null) {
      return;
    }
    for (Long genreId : genreIds) {
      entityManager.persist(new BookGenreEntity(bookId, genreId));
    }
  }

  private void replaceTags(EntityManager entityManager, long bookId, List<String> tags) {
    entityManager
        .createQuery("DELETE FROM BookTagEntity bt WHERE bt.bookId = :bookId")
        .setParameter("bookId", bookId)
        .executeUpdate();
    if (tags == null) {
      return;
    }
    for (String tag : tags) {
      if (tag != null && !tag.isBlank()) {
        entityManager.persist(new BookTagEntity(bookId, upsertTag(entityManager, tag.trim())));
      }
    }
  }

  private long upsertTag(EntityManager entityManager, String name) {
    String slug = name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    if (slug.isBlank()) {
      slug = "tag";
    }
    Optional<TagEntity> existing =
        entityManager
            .createQuery("SELECT t FROM TagEntity t WHERE t.slug = :slug", TagEntity.class)
            .setParameter("slug", slug)
            .getResultStream()
            .findFirst();
    if (existing.isPresent()) {
      TagEntity tag = existing.get();
      tag.setName(name);
      return tag.getId();
    }
    TagEntity tag = new TagEntity();
    tag.setName(name);
    tag.setSlug(slug);
    entityManager.persist(tag);
    entityManager.flush();
    return tag.getId();
  }

  private BookRecord map(EntityManager entityManager, BookEntity book) {
    return new BookRecord(
        book.getId(),
        book.getGoogleVolumeId(),
        book.getTitle(),
        authors(entityManager, book.getId()),
        book.getDescription(),
        book.getPublisher(),
        book.getPublishedDate(),
        book.getIsbn10(),
        book.getIsbn13(),
        book.getThumbnailUrl(),
        genres(entityManager, book.getId()),
        tags(entityManager, book.getId()));
  }

  private List<String> authors(EntityManager entityManager, long bookId) {
    return entityManager
        .createQuery(
            "SELECT a.authorName FROM BookAuthorEntity a WHERE a.bookId = :bookId ORDER BY a.displayOrder",
            String.class)
        .setParameter("bookId", bookId)
        .getResultList();
  }

  private List<GenreResponse> genres(EntityManager entityManager, long bookId) {
    return entityManager
        .createQuery(
            """
                        SELECT g FROM GenreEntity g
                        WHERE g.id IN (
                            SELECT bg.genreId FROM BookGenreEntity bg WHERE bg.bookId = :bookId
                        )
                        ORDER BY g.name
                        """,
            GenreEntity.class)
        .setParameter("bookId", bookId)
        .getResultStream()
        .map(genre -> new GenreResponse(genre.getId(), genre.getName(), genre.getSlug()))
        .toList();
  }

  private List<TagResponse> tags(EntityManager entityManager, long bookId) {
    return entityManager
        .createQuery(
            """
                        SELECT t FROM TagEntity t
                        WHERE t.id IN (
                            SELECT bt.tagId FROM BookTagEntity bt WHERE bt.bookId = :bookId
                        )
                        ORDER BY t.name
                        """,
            TagEntity.class)
        .setParameter("bookId", bookId)
        .getResultStream()
        .map(tag -> new TagResponse(tag.getId(), tag.getName(), tag.getSlug()))
        .toList();
  }
}
