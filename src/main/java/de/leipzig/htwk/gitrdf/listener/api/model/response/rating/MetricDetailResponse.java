package de.leipzig.htwk.gitrdf.listener.api.model.response.rating;

import java.util.List;

public class MetricDetailResponse {
  private Long orderId;
  private String owner;
  private String repository;
  private String metricId;
  private String metricName;
  private Integer metricVersion;
  private Integer ratingCount;
  private List<RatingDetail> ratings;

  // Getters and setters (same pattern as above)
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

  public String getMetricId() {
    return metricId;
  }

  public void setMetricId(String metricId) {
    this.metricId = metricId;
  }

  public String getMetricName() {
    return metricName;
  }

  public void setMetricName(String metricName) {
    this.metricName = metricName;
  }

  public Integer getMetricVersion() {
    return metricVersion;
  }

  public void setMetricVersion(Integer metricVersion) {
    this.metricVersion = metricVersion;
  }

  public Integer getRatingCount() {
    return ratingCount;
  }

  public void setRatingCount(Integer ratingCount) {
    this.ratingCount = ratingCount;
  }

  public List<RatingDetail> getRatings() {
    return ratings;
  }

  public void setRatings(List<RatingDetail> ratings) {
    this.ratings = ratings;
  }
}