package de.leipzig.htwk.gitrdf.listener.api.model.response;

import java.time.LocalDateTime;
import java.util.List;

import de.leipzig.htwk.gitrdf.database.common.entity.GithubRepositoryOrderRatingEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

@Value
public class GithubRepositoryOrderRatingResponse {

  public static GithubRepositoryOrderRatingResponse from(GithubRepositoryOrderRatingEntity entity) {
    return new GithubRepositoryOrderRatingResponse(
        entity.getId(),
        entity.getGithubRepositoryOrder().getId(),
        entity.getGithubRepositoryOrder().getOwnerName(),
        entity.getGithubRepositoryOrder().getRepositoryName(),
        entity.getMetricId(),
        entity.getMetricName(),
        entity.getMetricVersion(),
        entity.getTaskSessionId(),
        entity.getCreatedAt(),
        entity.getRdfBlob() != null);
  }

  public static List<GithubRepositoryOrderRatingResponse> toList(List<GithubRepositoryOrderRatingEntity> entities) {
    return entities.stream().map(GithubRepositoryOrderRatingResponse::from).toList();
  }

  @Schema(description = "The unique ID of the rating")
  Long id;

  @Schema(description = "The ID of the GitHub repository order this rating belongs to")
  Long githubRepositoryOrderId;

  @Schema(description = "The owner name of the repository", example = "dotnet")
  String ownerName;

  @Schema(description = "The repository name", example = "core")
  String repositoryName;

  @Schema(description = "The metric ID used for this rating", example = "code_quality_metric")
  String metricId;

  @Schema(description = "The human-readable name of the metric", example = "Code Quality Assessment")
  String metricName;

  @Schema(description = "The version of the metric used")
  Integer metricVersion;

  @Schema(description = "The task session ID that generated this rating")
  String taskSessionId;

  @Schema(description = "When this rating was created")
  LocalDateTime createdAt;

  @Schema(description = "Whether this rating has RDF data available")
  boolean hasRdfData;
}