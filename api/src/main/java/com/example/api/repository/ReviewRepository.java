package com.example.api.repository;

import com.example.api.entity.ReviewEntity;
import com.example.api.model.ReviewRecord;
import com.example.api.persistence.JpaExecutor;
import com.google.inject.Inject;
import java.sql.Timestamp;
import java.util.List;

public final class ReviewRepository {
  private final JpaExecutor jpa;
  private final UserRepository userRepository;

  @Inject
  public ReviewRepository(JpaExecutor jpa, UserRepository userRepository) {
    this.jpa = jpa;
    this.userRepository = userRepository;
  }

  public void upsert(long userId, long bookId, int rating, String body) {
    jpa.writeVoid(
        entityManager -> {
          ReviewEntity review =
              entityManager
                  .createQuery(
                      "SELECT r FROM ReviewEntity r WHERE r.userId = :userId AND r.bookId = :bookId",
                      ReviewEntity.class)
                  .setParameter("userId", userId)
                  .setParameter("bookId", bookId)
                  .getResultStream()
                  .findFirst()
                  .orElseGet(ReviewEntity::new);
          review.setUserId(userId);
          review.setBookId(bookId);
          review.setRating(rating);
          review.setBody(body);
          if (review.getId() == null) {
            entityManager.persist(review);
          }
        });
  }

  public void delete(long userId, long bookId) {
    jpa.writeVoid(
        entityManager ->
            entityManager
                .createQuery(
                    "DELETE FROM ReviewEntity r WHERE r.userId = :userId AND r.bookId = :bookId")
                .setParameter("userId", userId)
                .setParameter("bookId", bookId)
                .executeUpdate());
  }

  public List<ReviewRecord> byBook(long bookId) {
    return jpa.read(
        entityManager ->
            entityManager
                .createQuery(
                    "SELECT r FROM ReviewEntity r WHERE r.bookId = :bookId ORDER BY r.updatedAt DESC",
                    ReviewEntity.class)
                .setParameter("bookId", bookId)
                .getResultStream()
                .map(
                    review ->
                        new ReviewRecord(
                            review.getId(),
                            review.getBookId(),
                            userRepository.findById(review.getUserId()).orElseThrow(),
                            review.getRating(),
                            review.getBody(),
                            timestamp(review.getCreatedAt()),
                            timestamp(review.getUpdatedAt())))
                .toList());
  }

  private String timestamp(Timestamp timestamp) {
    return timestamp == null ? null : timestamp.toInstant().toString();
  }
}
