package com.example.api.repository;

import com.example.api.dto.RecommendationDtos.ItemRequest;
import com.example.api.dto.RecommendationDtos.RecommendationListRequest;
import com.example.api.entity.QRecommendationListEntity;
import com.example.api.entity.QRecommendationListItemEntity;
import com.example.api.entity.RecommendationListEntity;
import com.example.api.entity.RecommendationListItemEntity;
import com.example.api.error.ApiException;
import com.example.api.model.RecommendationItemRecord;
import com.example.api.model.RecommendationListRecord;
import com.example.api.persistence.JpaExecutor;
import com.google.inject.Inject;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public final class RecommendationRepository {
  private final JpaExecutor jpa;
  private final UserRepository userRepository;
  private final BookRepository bookRepository;

  @Inject
  public RecommendationRepository(
      JpaExecutor jpa, UserRepository userRepository, BookRepository bookRepository) {
    this.jpa = jpa;
    this.userRepository = userRepository;
    this.bookRepository = bookRepository;
  }

  public RecommendationListRecord create(long userId, RecommendationListRequest request) {
    return jpa.write(
        entityManager -> {
          RecommendationListEntity list = new RecommendationListEntity();
          list.setUserId(userId);
          list.setTitle(request.title());
          list.setDescription(request.description());
          list.setVisibility(request.visibility());
          entityManager.persist(list);
          entityManager.flush();
          replaceItems(entityManager, list.getId(), request.items());
          entityManager.flush();
          return map(entityManager, list);
        });
  }

  public RecommendationListRecord update(
      long ownerId, long listId, RecommendationListRequest request) {
    return jpa.write(
        entityManager -> {
          RecommendationListEntity list =
              entityManager.find(RecommendationListEntity.class, listId);
          if (list == null) {
            throw ApiException.notFound("Recommendation list was not found.");
          }
          if (!list.getUserId().equals(ownerId)) {
            throw ApiException.forbidden("Only the owner can change this recommendation list.");
          }
          list.setTitle(request.title());
          list.setDescription(request.description());
          list.setVisibility(request.visibility());
          replaceItems(entityManager, listId, request.items());
          return map(entityManager, list);
        });
  }

  public void delete(long ownerId, long listId) {
    jpa.writeVoid(
        entityManager -> {
          RecommendationListEntity list =
              entityManager.find(RecommendationListEntity.class, listId);
          if (list == null) {
            throw ApiException.notFound("Recommendation list was not found.");
          }
          if (!list.getUserId().equals(ownerId)) {
            throw ApiException.forbidden("Only the owner can change this recommendation list.");
          }
          entityManager.remove(list);
        });
  }

  public Optional<RecommendationListRecord> findById(long listId) {
    return jpa.read(
        entityManager ->
            Optional.ofNullable(entityManager.find(RecommendationListEntity.class, listId))
                .map(list -> map(entityManager, list)));
  }

  public List<RecommendationListRecord> listByUser(long userId, boolean includePrivate) {
    return jpa.read(
        entityManager -> {
          QRecommendationListEntity list = QRecommendationListEntity.recommendationListEntity;
          List<RecommendationListEntity> lists =
              new JPAQueryFactory(entityManager)
                  .select(list)
                  .from(list)
                  .where(
                      includePrivate
                          ? list.userId.eq(userId)
                          : list.userId.eq(userId).and(list.visibility.eq("public")))
                  .orderBy(list.updatedAt.desc(), list.id.desc())
                  .fetch();
          return lists.stream().map(row -> map(entityManager, row)).toList();
        });
  }

  public boolean isOwner(long listId, long userId) {
    return jpa.read(
        entityManager -> {
          Long count =
              entityManager
                  .createQuery(
                      "SELECT COUNT(l) FROM RecommendationListEntity l WHERE l.id = :listId AND l.userId = :userId",
                      Long.class)
                  .setParameter("listId", listId)
                  .setParameter("userId", userId)
                  .getSingleResult();
          return count > 0;
        });
  }

  private void replaceItems(EntityManager entityManager, long listId, List<ItemRequest> items) {
    entityManager
        .createQuery("DELETE FROM RecommendationListItemEntity i WHERE i.listId = :listId")
        .setParameter("listId", listId)
        .executeUpdate();
    if (items == null) {
      return;
    }
    for (ItemRequest item : items) {
      entityManager.persist(
          new RecommendationListItemEntity(
              listId, item.bookId(), item.comment(), item.displayOrder()));
    }
  }

  private RecommendationListRecord map(EntityManager entityManager, RecommendationListEntity list) {
    return new RecommendationListRecord(
        list.getId(),
        userRepository.findById(list.getUserId()).orElseThrow(),
        list.getTitle(),
        list.getDescription(),
        list.getVisibility(),
        items(entityManager, list.getId()));
  }

  private List<RecommendationItemRecord> items(EntityManager entityManager, long listId) {
    QRecommendationListItemEntity item = QRecommendationListItemEntity.recommendationListItemEntity;
    List<RecommendationListItemEntity> items =
        new JPAQueryFactory(entityManager)
            .select(item)
            .from(item)
            .where(item.listId.eq(listId))
            .orderBy(item.displayOrder.asc())
            .fetch();
    return items.stream()
        .map(
            row ->
                new RecommendationItemRecord(
                    bookRepository.findById(row.getBookId()).orElseThrow(),
                    row.getComment(),
                    row.getDisplayOrder()))
        .toList();
  }
}
