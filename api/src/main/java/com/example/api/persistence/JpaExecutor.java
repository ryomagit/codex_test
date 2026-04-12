package com.example.api.persistence;

import com.example.api.error.Db;
import com.google.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.function.Consumer;
import java.util.function.Function;

public final class JpaExecutor {
  private final EntityManagerFactory entityManagerFactory;

  @Inject
  public JpaExecutor(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  public <T> T read(Function<EntityManager, T> work) {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    try {
      return work.apply(entityManager);
    } catch (RuntimeException exception) {
      throw Db.failure(exception);
    } finally {
      entityManager.close();
    }
  }

  public <T> T write(Function<EntityManager, T> work) {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    try {
      entityManager.getTransaction().begin();
      T result = work.apply(entityManager);
      entityManager.getTransaction().commit();
      return result;
    } catch (RuntimeException exception) {
      if (entityManager.getTransaction().isActive()) {
        entityManager.getTransaction().rollback();
      }
      throw Db.failure(exception);
    } finally {
      entityManager.close();
    }
  }

  public void writeVoid(Consumer<EntityManager> work) {
    write(
        entityManager -> {
          work.accept(entityManager);
          return null;
        });
  }
}
