package com.example.api;

import com.example.api.resource.HealthResource;
import com.example.api.resource.HelloResource;
import com.example.api.service.GreetingService;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

public final class AppConfig {
    private AppConfig() {
    }

    public static ResourceConfig create() {
        Injector injector = Guice.createInjector(new ApiModule());
        GreetingService greetingService = injector.getInstance(GreetingService.class);

        return new ResourceConfig()
                .register(HealthResource.class)
                .register(HelloResource.class)
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bind(greetingService).to(GreetingService.class);
                    }
                });
    }
}
