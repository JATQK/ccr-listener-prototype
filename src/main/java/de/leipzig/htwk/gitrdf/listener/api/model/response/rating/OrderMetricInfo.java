package de.leipzig.htwk.gitrdf.listener.api.model.response.rating;

import java.time.LocalDateTime;

public class OrderMetricInfo {
  private Long orderId;
  private String owner;
  private String repository;
  private Integer ratingCount;
  private Boolean hasRdfData;
  private LocalDateTime latestRating;

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

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

  public Boolean getHasRdfData() {
    return hasRdfData;
  }

  public void setHasRdfData(Boolean hasRdfData) {
    this.hasRdfData = hasRdfData;
  }

  public LocalDateTime getLatestRating() {
    return latestRating;
  }

  public void setLatestRating(LocalDateTime latestRating) {
    this.latestRating = latestRating;
  }
}
