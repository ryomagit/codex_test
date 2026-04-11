package com.example.api;

import java.net.URI;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws InterruptedException {
        String host = System.getenv().getOrDefault("HOST", "0.0.0.0");
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        URI baseUri = URI.create("http://" + host + ":" + port + "/");
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, AppConfig.create());

        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
        System.out.println("API server started: " + baseUri);
        Thread.currentThread().join();
    }
}
