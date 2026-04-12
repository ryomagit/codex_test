package com.example.api.repository;

import com.example.api.entity.QUserEntity;
import com.example.api.entity.QUserFollowEntity;
import com.example.api.entity.UserEntity;
import com.example.api.entity.UserFollowEntity;
import com.example.api.entity.UserFollowId;
import com.example.api.entity.UserProfileEntity;
import com.example.api.error.ApiException;
import com.example.api.model.UserRecord;
import com.example.api.persistence.JpaExecutor;
import com.google.inject.Inject;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;

public final class UserRepository {
  private final JpaExecutor jpa;

  @Inject
  public UserRepository(JpaExecutor jpa) {
    this.jpa = jpa;
  }

  public UserRecord create(
      String email, String displayName, String passwordHash, String passwordSalt) {
    return jpa.write(
        entityManager -> {
          UserEntity user = new UserEntity();
          user.setEmail(email);
          user.setDisplayName(displayName);
          user.setPasswordHash(passwordHash);
          user.setPasswordSalt(passwordSalt);
          entityManager.persist(user);
          entityManager.flush();
          return map(entityManager, user);
        });
  }

  public Optional<UserRecord> findByEmail(String email) {
    return jpa.read(
        entityManager ->
            entityManager
                .createQuery("SELECT u FROM UserEntity u WHERE u.email = :email", UserEntity.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst()
                .map(user -> map(entityManager, user)));
  }

  public Optional<UserRecord> findById(long id) {
    return jpa.read(
        entityManager ->
            Optional.ofNullable(entityManager.find(UserEntity.class, id))
                .map(user -> map(entityManager, user)));
  }

  public List<UserRecord> featured() {
    return jpa.read(
        entityManager -> {
          QUserEntity user = QUserEntity.userEntity;
          QUserFollowEntity follow = QUserFollowEntity.userFollowEntity;
          List<UserEntity> entities =
              new JPAQueryFactory(entityManager)
                  .select(user)
                  .from(user)
                  .leftJoin(follow)
                  .on(follow.followedUserId.eq(user.id))
                  .groupBy(user.id)
                  .orderBy(follow.followerUserId.count().desc(), user.id.asc())
                  .limit(20)
                  .fetch();
          return entities.stream().map(entity -> map(entityManager, entity)).toList();
        });
  }

  public void follow(long followerId, long followedId) {
    if (followerId == followedId) {
      throw ApiException.badRequest("You cannot follow yourself.");
    }
    if (findById(followedId).isEmpty()) {
      throw ApiException.notFound("User was not found.");
    }
    jpa.writeVoid(
        entityManager -> entityManager.persist(new UserFollowEntity(followerId, followedId)));
  }

  public void unfollow(long followerId, long followedId) {
    jpa.writeVoid(
        entityManager -> {
          UserFollowEntity follow =
              entityManager.find(UserFollowEntity.class, new UserFollowId(followerId, followedId));
          if (follow != null) {
            entityManager.remove(follow);
          }
        });
  }

  private UserRecord map(jakarta.persistence.EntityManager entityManager, UserEntity user) {
    UserProfileEntity profile = entityManager.find(UserProfileEntity.class, user.getId());
    long followerCount = followerCount(entityManager, user.getId());
    return new UserRecord(
        user.getId(),
        user.getEmail(),
        user.getDisplayName(),
        user.getPasswordHash(),
        user.getPasswordSalt(),
        profile == null ? null : profile.getBio(),
        profile == null ? null : profile.getAvatarUrl(),
        profile == null ? null : profile.getHeadline(),
        followerCount);
  }

  private long followerCount(jakarta.persistence.EntityManager entityManager, long userId) {
    QUserFollowEntity follow = QUserFollowEntity.userFollowEntity;
    Long count =
        new JPAQueryFactory(entityManager)
            .select(follow.followerUserId.count())
            .from(follow)
            .where(follow.followedUserId.eq(userId))
            .fetchOne();
    return count == null ? 0 : count;
  }
}
