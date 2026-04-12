package com.example.api.model;

public record UserRecord(
    long id,
    String email,
    String displayName,
    String passwordHash,
    String passwordSalt,
    String bio,
    String avatarUrl,
    String headline,
    long followerCount) {}
