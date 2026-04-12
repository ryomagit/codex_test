package com.example.api;

import com.example.api.error.ApiExceptionMapper;
import com.example.api.error.GenericExceptionMapper;
import com.example.api.resource.AuthResource;
import com.example.api.resource.BookResource;
import com.example.api.resource.GenreResource;
import com.example.api.resource.HealthResource;
import com.example.api.resource.HelloResource;
import com.example.api.resource.MeResource;
import com.example.api.resource.RecommendationListResource;
import com.example.api.resource.UserResource;
import com.example.api.service.AuthService;
import com.example.api.service.BookService;
import com.example.api.service.FavoriteService;
import com.example.api.service.GenreService;
import com.example.api.service.GreetingService;
import com.example.api.service.RecommendationService;
import com.example.api.service.ReviewService;
import com.example.api.service.UserService;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public final class AppConfig {
  private AppConfig() {}

  public static ResourceConfig create() {
    Injector injector = Guice.createInjector(new ApiModule());
    GreetingService greetingService = injector.getInstance(GreetingService.class);
    AuthService authService = injector.getInstance(AuthService.class);
    UserService userService = injector.getInstance(UserService.class);
    BookService bookService = injector.getInstance(BookService.class);
    FavoriteService favoriteService = injector.getInstance(FavoriteService.class);
    ReviewService reviewService = injector.getInstance(ReviewService.class);
    GenreService genreService = injector.getInstance(GenreService.class);
    RecommendationService recommendationService = injector.getInstance(RecommendationService.class);

    return new ResourceConfig()
        .register(JacksonFeature.class)
        .register(HealthResource.class)
        .register(HelloResource.class)
        .register(AuthResource.class)
        .register(UserResource.class)
        .register(BookResource.class)
        .register(MeResource.class)
        .register(GenreResource.class)
        .register(RecommendationListResource.class)
        .register(new ApiExceptionMapper())
        .register(new GenericExceptionMapper())
        .register(
            new AbstractBinder() {
              @Override
              protected void configure() {
                bind(greetingService).to(GreetingService.class);
                bind(authService).to(AuthService.class);
                bind(userService).to(UserService.class);
                bind(bookService).to(BookService.class);
                bind(favoriteService).to(FavoriteService.class);
                bind(reviewService).to(ReviewService.class);
                bind(genreService).to(GenreService.class);
                bind(recommendationService).to(RecommendationService.class);
              }
            });
  }
}
