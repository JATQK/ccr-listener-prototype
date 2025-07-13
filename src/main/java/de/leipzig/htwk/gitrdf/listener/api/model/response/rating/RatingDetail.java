package de.leipzig.htwk.gitrdf.listener.api.model.response.rating;

import java.time.LocalDateTime;
import java.util.Map;

public class RatingDetail {
  private Long id;
  private String metricId;
  private String metricName;
  private Integer metricVersion;
  private String taskSessionId;
  private LocalDateTime createdAt;
  private Boolean hasRdfData;
  private Map<String, Object> filters;

  // Getters and setters (same pattern)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public String getTaskSessionId() {
    return taskSessionId;
  }

  public void setTaskSessionId(String taskSessionId) {
    this.taskSessionId = taskSessionId;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public Boolean getHasRdfData() {
    return hasRdfData;
  }

  public void setHasRdfData(Boolean hasRdfData) {
    this.hasRdfData = hasRdfData;
  }

  public Map<String, Object> getFilters() {
    return filters;
  }

  public void setFilters(Map<String, Object> filters) {
    this.filters = filters;
  }
}