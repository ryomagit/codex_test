package com.example.api.service;

import com.example.api.dto.RecommendationDtos.RecommendationListRequest;
import com.example.api.dto.RecommendationDtos.RecommendationListResponse;
import com.example.api.error.ApiException;
import com.example.api.model.RecommendationListRecord;
import com.example.api.repository.RecommendationRepository;
import com.example.api.security.CurrentUser;
import com.google.inject.Inject;
import java.util.List;

public final class RecommendationService {
  private final RecommendationRepository recommendations;

  @Inject
  public RecommendationService(RecommendationRepository recommendations) {
    this.recommendations = recommendations;
  }

  public RecommendationListResponse create(CurrentUser user, RecommendationListRequest request) {
    return Responses.recommendation(recommendations.create(user.id(), normalize(request)));
  }

  public RecommendationListResponse update(
      CurrentUser user, long listId, RecommendationListRequest request) {
    return Responses.recommendation(recommendations.update(user.id(), listId, normalize(request)));
  }

  public void delete(CurrentUser user, long listId) {
    recommendations.delete(user.id(), listId);
  }

  public RecommendationListResponse get(CurrentUser viewer, long listId) {
    RecommendationListRecord list =
        recommendations
            .findById(listId)
            .orElseThrow(() -> ApiException.notFound("Recommendation list was not found."));
    if ("private".equals(list.visibility())
        && (viewer == null || viewer.id() != list.user().id())) {
      throw ApiException.forbidden("This recommendation list is private.");
    }
    return Responses.recommendation(list);
  }

  public List<RecommendationListResponse> listByUser(CurrentUser viewer, long userId) {
    boolean includePrivate = viewer != null && viewer.id() == userId;
    return recommendations.listByUser(userId, includePrivate).stream()
        .map(Responses::recommendation)
        .toList();
  }

  private RecommendationListRequest normalize(RecommendationListRequest request) {
    if (request == null) {
      throw ApiException.badRequest("Request body is required.");
    }
    return new RecommendationListRequest(
        Validation.required(request.title(), "title"),
        request.description(),
        Validation.visibility(request.visibility()),
        request.items());
  }
}
