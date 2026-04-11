package com.example.api;

import com.example.api.service.DefaultGreetingService;
import com.example.api.service.GreetingService;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

final class ApiModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GreetingService.class).to(DefaultGreetingService.class).in(Singleton.class);
    }
}

