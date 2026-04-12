package com.example.api.repository;

import com.example.api.entity.SessionEntity;
import com.example.api.model.UserRecord;
import com.example.api.persistence.JpaExecutor;
import com.google.inject.Inject;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public final class SessionRepository {
  private final JpaExecutor jpa;
  private final UserRepository userRepository;

  @Inject
  public SessionRepository(JpaExecutor jpa, UserRepository userRepository) {
    this.jpa = jpa;
    this.userRepository = userRepository;
  }

  public void create(long userId, String tokenHash) {
    jpa.writeVoid(
        entityManager -> {
          SessionEntity session = new SessionEntity();
          session.setUserId(userId);
          session.setTokenHash(tokenHash);
          session.setExpiresAt(Timestamp.from(Instant.now().plus(30, ChronoUnit.DAYS)));
          entityManager.persist(session);
        });
  }

  public Optional<UserRecord> findUserByTokenHash(String tokenHash) {
    return jpa.read(
        entityManager ->
            entityManager
                .createQuery(
                    "SELECT s FROM SessionEntity s WHERE s.tokenHash = :tokenHash AND s.expiresAt > :now",
                    SessionEntity.class)
                .setParameter("tokenHash", tokenHash)
                .setParameter("now", Timestamp.from(Instant.now()))
                .getResultStream()
                .findFirst()
                .flatMap(session -> userRepository.findById(session.getUserId())));
  }

  public void delete(String tokenHash) {
    jpa.writeVoid(
        entityManager ->
            entityManager
                .createQuery("DELETE FROM SessionEntity s WHERE s.tokenHash = :tokenHash")
                .setParameter("tokenHash", tokenHash)
                .executeUpdate());
  }
}
