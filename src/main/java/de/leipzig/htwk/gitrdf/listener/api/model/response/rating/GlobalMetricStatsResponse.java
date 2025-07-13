package de.leipzig.htwk.gitrdf.listener.api.model.response.rating;

import java.time.LocalDateTime;
import java.util.Map;

public class GlobalMetricStatsResponse {
  private String metricId;
  private String metricName;
  private Integer metricVersion;
  private Integer totalRatings;
  private Integer ratingsWithRdfData;
  private Integer uniqueOrders;
  private LocalDateTime earliestRating;
  private LocalDateTime latestRating;
  private Map<String, Long> taskSessions;
  private Map<Long, OrderStats> orderBreakdown;

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

  public Integer getTotalRatings() {
    return totalRatings;
  }

  public void setTotalRatings(Integer totalRatings) {
    this.totalRatings = totalRatings;
  }

  public Integer getRatingsWithRdfData() {
    return ratingsWithRdfData;
  }

  public void setRatingsWithRdfData(Integer ratingsWithRdfData) {
    this.ratingsWithRdfData = ratingsWithRdfData;
  }

  public Integer getUniqueOrders() {
    return uniqueOrders;
  }

  public void setUniqueOrders(Integer uniqueOrders) {
    this.uniqueOrders = uniqueOrders;
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

  public Map<Long, OrderStats> getOrderBreakdown() {
    return orderBreakdown;
  }

  public void setOrderBreakdown(Map<Long, OrderStats> orderBreakdown) {
    this.orderBreakdown = orderBreakdown;
  }
}
