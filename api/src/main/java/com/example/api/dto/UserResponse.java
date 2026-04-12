package com.example.api.dto;

public record UserResponse(
    long id,
    String email,
    String displayName,
    String bio,
    String avatarUrl,
    String headline,
    long followerCount) {}
