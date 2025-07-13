package de.leipzig.htwk.gitrdf.listener.api.model.response.rating;

import java.time.LocalDateTime;
import java.util.Map;

public class OrderStatsResponse {
  private Long orderId;
  private String owner;
  private String repository;
  private String status;
  private Integer totalRatings;
  private Integer uniqueMetrics;
  private Integer ratingsWithRdfData;
  private LocalDateTime earliestRating;
  private LocalDateTime latestRating;
  private Map<String, Long> taskSessions;
  private Map<String, MetricStats> metricBreakdown;

  // Getters and setters following same pattern...
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

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Integer getTotalRatings() {
    return totalRatings;
  }

  public void setTotalRatings(Integer totalRatings) {
    this.totalRatings = totalRatings;
  }

  public Integer getUniqueMetrics() {
    return uniqueMetrics;
  }

  public void setUniqueMetrics(Integer uniqueMetrics) {
    this.uniqueMetrics = uniqueMetrics;
  }

  public Integer getRatingsWithRdfData() {
    return ratingsWithRdfData;
  }

  public void setRatingsWithRdfData(Integer ratingsWithRdfData) {
    this.ratingsWithRdfData = ratingsWithRdfData;
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

  public Map<String, Long> getTaskSessions() {
    return taskSessions;
  }

  public void setTaskSessions(Map<String, Long> taskSessions) {
    this.taskSessions = taskSessions;
  }

  public Map<String, MetricStats> getMetricBreakdown() {
    return metricBreakdown;
  }

  public void setMetricBreakdown(Map<String, MetricStats> metricBreakdown) {
    this.metricBreakdown = metricBreakdown;
  }
}