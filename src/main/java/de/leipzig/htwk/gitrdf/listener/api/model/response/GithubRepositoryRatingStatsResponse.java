package de.leipzig.htwk.gitrdf.listener.api.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

@Value
public class GithubRepositoryRatingStatsResponse {

    @Schema(description = "The GitHub repository order ID these stats are for")
    Long githubRepositoryOrderId;

    @Schema(description = "The owner name of the repository", example = "dotnet")
    String ownerName;

    @Schema(description = "The repository name", example = "core")
    String repositoryName;

    @Schema(description = "Total number of ratings for this repository order")
    long totalRatings;

    @Schema(description = "Number of unique metrics used")
    long uniqueMetrics;

    @Schema(description = "Number of unique task sessions")
    long uniqueSessions;
}