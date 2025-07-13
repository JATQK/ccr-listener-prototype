package de.leipzig.htwk.gitrdf.listener.api.model.response.rating;

import java.util.List;

public class GlobalMetricResponse {
  private String metricId;
  private String metricName;
  private Integer metricVersion;
  private Integer totalRatings;
  private Integer uniqueOrders;
  private List<OrderMetricInfo> orders;

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

  public Integer getUniqueOrders() {
    return uniqueOrders;
  }

  public void setUniqueOrders(Integer uniqueOrders) {
    this.uniqueOrders = uniqueOrders;
  }

  public List<OrderMetricInfo> getOrders() {
    return orders;
  }

  public void setOrders(List<OrderMetricInfo> orders) {
    this.orders = orders;
  }
}