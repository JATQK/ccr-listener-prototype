package de.leipzig.htwk.gitrdf.listener.api.model.response.rating;

import java.time.LocalDateTime;

public class OrderStats {
  private String owner;
  private String repository;
  private Integer ratingCount;
  private Integer withRdfData;
  private LocalDateTime earliestRating;
  private LocalDateTime latestRating;

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getRepository() {
    return repository;
  }

  public void setRepository(String repository) {
    this.repository = repository;
  }

  public Integer getRatingCount() {
    return ratingCount;
  }

  public void setRatingCount(Integer ratingCount) {
    this.ratingCount = ratingCount;
  }

  public Integer getWithRdfData() {
    return withRdfData;
  }

  public void setWithRdfData(Integer withRdfData) {
    this.withRdfData = withRdfData;
  }

  public LocalDateTime getEarliestRating() {
    return earliestRating;
  }

  public void setEarliestRating(LocalDateTime earliestRating) {
    this.earliestRating = earliestRating;
  }

  public LocalDateTime getLatestRating() {
    return latestRating;
  }

  public void setLatestRating(LocalDateTime latestRating) {
    this.latestRating = latestRating;
  }
}