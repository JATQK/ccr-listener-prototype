package de.leipzig.htwk.gitrdf.listener.api.model.response.rating;

import java.util.List;

public class OrderRatingsResponse {
  private Long orderId;
  private String owner;
  private String repository;
  private String status;
  private Integer totalRatings;
  private Integer uniqueMetrics;
  private List<MetricSummary> metrics;

  // Getters and setters
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

  public List<MetricSummary> getMetrics() {
    return metrics;
  }

  public void setMetrics(List<MetricSummary> metrics) {
    this.metrics = metrics;
  }
}