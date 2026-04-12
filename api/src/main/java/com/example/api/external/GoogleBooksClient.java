package com.example.api.external;

import com.example.api.dto.BookDtos.GoogleBookResponse;
import com.example.api.error.ApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class GoogleBooksClient {
  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  @Inject
  public GoogleBooksClient(ObjectMapper objectMapper) {
    this.httpClient = HttpClient.newHttpClient();
    this.objectMapper = objectMapper;
  }

  public List<GoogleBookResponse> search(String query) {
    String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(
                URI.create(
                    "https://www.googleapis.com/books/v1/volumes?q=" + encoded + "&maxResults=20"))
            .GET()
            .build();
    try {
      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() >= 400) {
        throw ApiException.badRequest("Google Books search failed.");
      }
      JsonNode root = objectMapper.readTree(response.body());
      List<GoogleBookResponse> books = new ArrayList<>();
      for (JsonNode item : root.path("items")) {
        JsonNode volume = item.path("volumeInfo");
        books.add(
            new GoogleBookResponse(
                item.path("id").asText(),
                volume.path("title").asText(),
                authors(volume.path("authors")),
                text(volume, "description"),
                text(volume, "publisher"),
                text(volume, "publishedDate"),
                isbn(volume, "ISBN_10"),
                isbn(volume, "ISBN_13"),
                text(volume.path("imageLinks"), "thumbnail")));
      }
      return books;
    } catch (IOException exception) {
      throw ApiException.badRequest("Google Books search failed.");
    } catch (InterruptedException exception) {
      Thread.currentThread().interrupt();
      throw ApiException.badRequest("Google Books search was interrupted.");
    }
  }

  private List<String> authors(JsonNode authors) {
    List<String> values = new ArrayList<>();
    for (JsonNode author : authors) {
      values.add(author.asText());
    }
    return values;
  }

  private String isbn(JsonNode volume, String type) {
    for (JsonNode identifier : volume.path("industryIdentifiers")) {
      if (type.equals(identifier.path("type").asText())) {
        return identifier.path("identifier").asText();
      }
    }
    return null;
  }

  private String text(JsonNode node, String name) {
    JsonNode value = node.path(name);
    return value.isMissingNode() || value.isNull() ? null : value.asText();
  }
}
