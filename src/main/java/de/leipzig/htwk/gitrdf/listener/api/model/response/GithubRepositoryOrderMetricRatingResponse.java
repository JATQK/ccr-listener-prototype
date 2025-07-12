package de.leipzig.htwk.gitrdf.listener.api.model.response;

import de.leipzig.htwk.gitrdf.database.common.entity.GithubRepositoryOrderMetricRatingEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class GithubRepositoryOrderMetricRatingResponse {

    public static GithubRepositoryOrderMetricRatingResponse from(GithubRepositoryOrderMetricRatingEntity entity) {
        return new GithubRepositoryOrderMetricRatingResponse(
                entity.getId(),
                entity.getMetricId(),
                entity.getMetricName(),
                entity.getRunMetadata(),
                entity.getCreatedAt(),
                entity.getTaskSessionId());
    }

    public static List<GithubRepositoryOrderMetricRatingResponse> toList(List<GithubRepositoryOrderMetricRatingEntity> entities) {
        return entities.stream().map(GithubRepositoryOrderMetricRatingResponse::from).toList();
    }

    long id;
    String metricId;
    String metricName;
    String runMetadata;
    LocalDateTime createdAt;
    @Schema(description = "Session id of the metric task")
    String taskSessionId;
}
