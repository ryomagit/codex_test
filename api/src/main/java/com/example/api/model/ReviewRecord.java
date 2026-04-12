package com.example.api.model;

public record ReviewRecord(
    long id,
    long bookId,
    UserRecord user,
    int rating,
    String body,
    String createdAt,
    String updatedAt) {}
