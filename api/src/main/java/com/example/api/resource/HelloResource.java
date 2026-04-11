package com.example.api.resource;

import com.example.api.service.GreetingService;
import com.google.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/hello")
public final class HelloResource {
    private final GreetingService greetingService;

    @Inject
    public HelloResource(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHello() {
        return Response.ok("{\"message\":\"" + greetingService.message() + "\"}", MediaType.APPLICATION_JSON).build();
    }
}
