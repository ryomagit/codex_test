package com.example.api.model;

import java.util.List;

public record RecommendationListRecord(
    long id,
    UserRecord user,
    String title,
    String description,
    String visibility,
    List<RecommendationItemRecord> items) {}
