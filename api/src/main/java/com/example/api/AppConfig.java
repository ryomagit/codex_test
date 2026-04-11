package com.example.api;

import com.example.api.resource.HealthResource;
import com.example.api.resource.HelloResource;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.glassfish.jersey.server.ResourceConfig;

public final class AppConfig {
    private AppConfig() {
    }

    public static ResourceConfig create() {
        Injector injector = Guice.createInjector(new ApiModule());

        return new ResourceConfig()
                .register(injector.getInstance(HealthResource.class))
                .register(injector.getInstance(HelloResource.class));
    }
}

