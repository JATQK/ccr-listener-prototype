package de.leipzig.htwk.gitrdf.listener.api.model.response.rating;

import java.time.LocalDateTime;

public class MetricSummary {
  private String metricId;
  private String metricName;
  private Integer metricVersion;
  private Integer ratingCount;
  private LocalDateTime latestCreated;
  private Boolean hasRdfData;

  // Getters and setters
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

  public LocalDateTime getLatestCreated() {
    return latestCreated;
  }

  public void setLatestCreated(LocalDateTime latestCreated) {
    this.latestCreated = latestCreated;
  }

  public Boolean getHasRdfData() {
    return hasRdfData;
  }

  public void setHasRdfData(Boolean hasRdfData) {
    this.hasRdfData = hasRdfData;
  }
}