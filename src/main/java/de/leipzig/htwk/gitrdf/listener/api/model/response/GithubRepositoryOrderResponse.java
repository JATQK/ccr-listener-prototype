package de.leipzig.htwk.gitrdf.listener.api.model.response;

import java.util.List;

import de.leipzig.htwk.gitrdf.database.common.entity.GithubRepositoryOrderEntity;
import de.leipzig.htwk.gitrdf.database.common.entity.enums.GitRepositoryOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

@Value
public class GithubRepositoryOrderResponse {

    public static GithubRepositoryOrderResponse from(GithubRepositoryOrderEntity entity) {
        return new GithubRepositoryOrderResponse(
                entity.getId(),
                entity.getStatus(),
                entity.getNumberOfTries(),
                entity.getOwnerName(),
                entity.getRepositoryName(),
                null, // ratingsCount - not included in basic response
                null // uniqueMetricsCount - not included in basic response
        );
    }

    public static GithubRepositoryOrderResponse fromWithRatings(GithubRepositoryOrderEntity entity, long ratingsCount,
            long uniqueMetricsCount) {
        return new GithubRepositoryOrderResponse(
                entity.getId(),
                entity.getStatus(),
                entity.getNumberOfTries(),
                entity.getOwnerName(),
                entity.getRepositoryName(),
                ratingsCount,
                uniqueMetricsCount);
    }

    public static List<GithubRepositoryOrderResponse> toList(List<GithubRepositoryOrderEntity> entities) {
        return entities.stream().map(GithubRepositoryOrderResponse::from).toList();
    }

    long id;
    GitRepositoryOrderStatus status;
    int numberOfTries;

    @Schema(example = "dotnet")
    String owner;

    @Schema(example = "core")
    String repository;

    @Schema(description = "Number of ratings for this repository (only included in with-ratings endpoints)")
    Long ratings;

    @Schema(description = "Number of statistics used for this repository (only included in with-ratings endpoints)")
    Long statistics;
}