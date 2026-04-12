package com.example.api.persistence;

import com.example.api.config.DatabaseConfig;
import com.google.inject.Provider;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public final class EntityManagerFactoryProvider implements Provider<EntityManagerFactory> {
  @Override
  public EntityManagerFactory get() {
    DatabaseConfig config = DatabaseConfig.fromEnv();
    Map<String, String> properties = new HashMap<>();
    properties.put("jakarta.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver");
    properties.put("jakarta.persistence.jdbc.url", config.jdbcUrl());
    properties.put("jakarta.persistence.jdbc.user", config.username());
    properties.put("jakarta.persistence.jdbc.password", config.password());
    return Persistence.createEntityManagerFactory("book-app", properties);
  }
}
