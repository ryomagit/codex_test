package com.example.api;

import com.example.api.persistence.EntityManagerFactoryProvider;
import com.example.api.security.PasswordHasher;
import com.example.api.security.SessionTokens;
import com.example.api.service.DefaultGreetingService;
import com.example.api.service.GreetingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import jakarta.persistence.EntityManagerFactory;

final class ApiModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(GreetingService.class).to(DefaultGreetingService.class).in(Singleton.class);
    bind(EntityManagerFactory.class)
        .toProvider(EntityManagerFactoryProvider.class)
        .in(Singleton.class);
    bind(ObjectMapper.class).in(Singleton.class);
    bind(PasswordHasher.class).in(Singleton.class);
    bind(SessionTokens.class).in(Singleton.class);
  }
}
