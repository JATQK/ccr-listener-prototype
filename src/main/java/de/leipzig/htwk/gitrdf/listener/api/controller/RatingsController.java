package de.leipzig.htwk.gitrdf.listener.api.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.leipzig.htwk.gitrdf.database.common.entity.GithubRepositoryOrderEntity;
import de.leipzig.htwk.gitrdf.database.common.entity.GithubRepositoryOrderRatingEntity;
import de.leipzig.htwk.gitrdf.listener.api.model.response.rating.GlobalMetricResponse;
import de.leipzig.htwk.gitrdf.listener.api.model.response.rating.GlobalMetricStatsResponse;
import de.leipzig.htwk.gitrdf.listener.api.model.response.rating.MetricDetailResponse;
import de.leipzig.htwk.gitrdf.listener.api.model.response.rating.MetricStats;
import de.leipzig.htwk.gitrdf.listener.api.model.response.rating.MetricSummary;
import de.leipzig.htwk.gitrdf.listener.api.model.response.rating.OrderMetricInfo;
import de.leipzig.htwk.gitrdf.listener.api.model.response.rating.OrderRatingsResponse;
import de.leipzig.htwk.gitrdf.listener.api.model.response.rating.OrderStats;
import de.leipzig.htwk.gitrdf.listener.api.model.response.rating.OrderStatsResponse;
import de.leipzig.htwk.gitrdf.listener.api.model.response.rating.RatingDetail;
import de.leipzig.htwk.gitrdf.listener.service.RatingsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/listener-service/api/v1/github/ratings")
@Tag(name = "Github Ratings API")
public class RatingsController {

  @Autowired
  private RatingsService ratingsService;

  // ============== ORDER-BASED ENDPOINTS ==============

  /**
   * 1. GET /ratings/{id} - Get List of saved metrics for the orderId
   */
  @GetMapping("/{id}")
  public ResponseEntity<OrderRatingsResponse> getOrderRatings(@PathVariable Long id) {
    log.debug("Getting ratings for order ID: {}", id);

    GithubRepositoryOrderEntity order = ratingsService.getOrderById(id);
    List<GithubRepositoryOrderRatingEntity> ratings = ratingsService.getRatingsByOrderId(id);
    RatingsService.OrderStatistics stats = ratingsService.getOrderStatistics(id);

    OrderRatingsResponse response = new OrderRatingsResponse();
    response.setOrderId(id);
    response.setOwner(order.getOwnerName());
    response.setRepository(order.getRepositoryName());
    response.setStatus(order.getStatus().toString());
    response.setTotalRatings(stats.getTotalRatings());
    response.setUniqueMetrics(stats.getUniqueMetrics());

    // Group ratings by metric ID for summary
    List<MetricSummary> metricSummaries = ratings.stream()
        .collect(Collectors.groupingBy(GithubRepositoryOrderRatingEntity::getMetricId))
        .entrySet().stream()
        .map(entry -> createMetricSummary(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());

    response.setMetrics(metricSummaries);

    return ResponseEntity.ok(response);
  }

  /**
   * 2. GET /ratings/{id}/{metricId} - Fetch all details for one metricId of the
   * orderId
   */
  @GetMapping("/{id}/{metricId}")
  public ResponseEntity<MetricDetailResponse> getOrderMetricDetail(@PathVariable Long id,
      @PathVariable String metricId) {
    log.debug("Getting metric detail for order ID: {} and metric ID: {}", id, metricId);

    GithubRepositoryOrderEntity order = ratingsService.getOrderById(id);
    List<GithubRepositoryOrderRatingEntity> ratings = ratingsService.getRatingsByOrderIdAndMetricId(id, metricId);

    GithubRepositoryOrderRatingEntity firstRating = ratings.get(0);

    MetricDetailResponse response = new MetricDetailResponse();
    response.setOrderId(id);
    response.setOwner(order.getOwnerName());
    response.setRepository(order.getRepositoryName());
    response.setMetricId(metricId);
    response.setMetricName(firstRating.getMetricName());
    response.setMetricVersion(firstRating.getMetricVersion());
    response.setRatingCount(ratings.size());

    List<RatingDetail> ratingDetails = ratings.stream()
        .map(this::convertToRatingDetail)
        .collect(Collectors.toList());

    response.setRatings(ratingDetails);

    return ResponseEntity.ok(response);
  }

  /**
   * 3. GET /ratings/{id}/stats - Shows stats for all metricIds of the orderId
   */
  @GetMapping("/{id}/stats")
  public ResponseEntity<OrderStatsResponse> getOrderStats(@PathVariable Long id) {
    log.debug("Getting stats for order ID: {}", id);

    GithubRepositoryOrderEntity order = ratingsService.getOrderById(id);
    List<GithubRepositoryOrderRatingEntity> ratings = ratingsService.getRatingsByOrderId(id);
    RatingsService.OrderStatistics stats = ratingsService.getOrderStatistics(id);

    OrderStatsResponse response = new OrderStatsResponse();
    response.setOrderId(id);
    response.setOwner(order.getOwnerName());
    response.setRepository(order.getRepositoryName());
    response.setStatus(order.getStatus().toString());
    response.setTotalRatings(stats.getTotalRatings());
    response.setUniqueMetrics(stats.getUniqueMetrics());
    response.setRatingsWithRdfData(stats.getRatingsWithRdf());

    if (!ratings.isEmpty()) {
      response.setEarliestRating(ratings.stream()
          .map(GithubRepositoryOrderRatingEntity::getCreatedAt)
          .min(LocalDateTime::compareTo).orElse(null));
      response.setLatestRating(ratings.stream()
          .map(GithubRepositoryOrderRatingEntity::getCreatedAt)
          .max(LocalDateTime::compareTo).orElse(null));
    }

    // Group by task session
    Map<String, Long> sessionStats = ratings.stream()
        .filter(r -> r.getTaskSessionId() != null)
        .collect(Collectors.groupingBy(
            GithubRepositoryOrderRatingEntity::getTaskSessionId,
            Collectors.counting()));
    response.setTaskSessions(sessionStats);

    // Group by metric for detailed breakdown
    Map<String, MetricStats> metricStats = ratings.stream()
        .collect(Collectors.groupingBy(GithubRepositoryOrderRatingEntity::getMetricId))
        .entrySet().stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> createMetricStats(entry.getValue())));
    response.setMetricBreakdown(metricStats);

    return ResponseEntity.ok(response);
  }

  /**
   * 4. GET /ratings/{id}/download - Download all RDF metrics of the orderId
   */
  @GetMapping("/{id}/download")
  @Transactional(readOnly = true)
  public ResponseEntity<Resource> downloadOrderRdf(@PathVariable Long id) {
    log.debug("Downloading all RDF for order ID: {}", id);

    try {
      GithubRepositoryOrderEntity order = ratingsService.getOrderById(id);
      List<GithubRepositoryOrderRatingEntity> ratings = ratingsService.getRatingsWithRdfByOrderId(id);

      RatingsService.RdfDownloadResult result = ratingsService.createRdfDownload(ratings, "order_" + id);

      if (!result.hasContent()) {
        log.debug("No RDF content found for order ID: {}", id);
        return ResponseEntity.noContent().build();
      }

      String filename = ratingsService.generateDownloadFilename("order", id, null, order);

      log.debug("Returning RDF download for order ID: {} with {} files, filename: {}",
          id, result.getFileCount(), filename);

      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
          .contentType(MediaType.TEXT_PLAIN)
          .body(result.getResource());

    } catch (SQLException | IOException e) {
      log.error("Error downloading RDF for order {}: {}", id, e.getMessage(), e);
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * 5. GET /ratings/{id}/download/{metricId} - Download RDF for specific metric
   * of orderId
   */
  @GetMapping("/{id}/download/{metricId}")
  @Transactional(readOnly = true)
  public ResponseEntity<Resource> downloadOrderMetricRdf(@PathVariable Long id, @PathVariable String metricId) {
    log.debug("Downloading RDF for order ID: {} and metric ID: {}", id, metricId);

    try {
      GithubRepositoryOrderEntity order = ratingsService.getOrderById(id);
      List<GithubRepositoryOrderRatingEntity> ratings = ratingsService.getRatingsWithRdfByOrderIdAndMetricId(id,
          metricId);

      RatingsService.RdfDownloadResult result = ratingsService.createRdfDownload(ratings,
          "order_" + id + "_metric_" + metricId);

      if (!result.hasContent()) {
        log.debug("No RDF content found for order ID: {} and metric ID: {}", id, metricId);
        return ResponseEntity.noContent().build();
      }

      String filename = ratingsService.generateDownloadFilename("order_metric", id, metricId, order);

      log.debug("Returning RDF download for order ID: {} and metric ID: {} with {} files, filename: {}",
          id, metricId, result.getFileCount(), filename);

      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
          .contentType(MediaType.TEXT_PLAIN)
          .body(result.getResource());

    } catch (SQLException | IOException e) {
      log.error("Error downloading RDF for order {} and metric {}: {}", id, metricId, e.getMessage(), e);
      return ResponseEntity.internalServerError().build();
    }
  }

  // ============== METRIC-BASED ENDPOINTS ==============

  /**
   * 6. GET /ratings/metrics/{metricId} - Fetch all details of the metricId across
   * all orders
   */
  @GetMapping("/metrics/{metricId}")
  public ResponseEntity<GlobalMetricResponse> getGlobalMetricDetail(@PathVariable String metricId) {
    log.debug("Getting global details for metric ID: {}", metricId);

    List<GithubRepositoryOrderRatingEntity> ratings = ratingsService.getRatingsByMetricId(metricId);
    RatingsService.MetricStatistics stats = ratingsService.getMetricStatistics(metricId);

    GithubRepositoryOrderRatingEntity firstRating = ratings.get(0);

    GlobalMetricResponse response = new GlobalMetricResponse();
    response.setMetricId(metricId);
    response.setMetricName(firstRating.getMetricName());
    response.setMetricVersion(firstRating.getMetricVersion());
    response.setTotalRatings(stats.getTotalRatings());
    response.setUniqueOrders(stats.getUniqueOrders());

    // Group by order
    List<OrderMetricInfo> orderInfos = ratings.stream()
        .collect(Collectors.groupingBy(r -> r.getGithubRepositoryOrder().getId()))
        .entrySet().stream()
        .map(entry -> createOrderMetricInfo(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());

    response.setOrders(orderInfos);

    return ResponseEntity.ok(response);
  }

  /**
   * 7. GET /ratings/metrics/{metricId}/download - Download all RDFs of the
   * metricId
   */
  @GetMapping("/metrics/{metricId}/download")
  @Transactional(readOnly = true)
  public ResponseEntity<Resource> downloadMetricRdf(@PathVariable String metricId) {
    log.debug("Downloading all RDF for metric ID: {}", metricId);

    try {
      List<GithubRepositoryOrderRatingEntity> ratings = ratingsService.getRatingsWithRdfByMetricId(metricId);

      RatingsService.RdfDownloadResult result = ratingsService.createRdfDownload(ratings, "metric_" + metricId);

      if (!result.hasContent()) {
        log.debug("No RDF content found for metric ID: {}", metricId);
        return ResponseEntity.noContent().build();
      }

      String filename = ratingsService.generateDownloadFilename("metric", null, metricId, null);

      log.debug("Returning RDF download for metric ID: {} with {} files, filename: {}",
          metricId, result.getFileCount(), filename);

      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
          .contentType(MediaType.TEXT_PLAIN)
          .body(result.getResource());

    } catch (SQLException | IOException e) {
      log.error("Error downloading RDF for metric {}: {}", metricId, e.getMessage(), e);
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * 8. GET /ratings/metrics/{metricId}/stats - Show stats for the metricId across
   * all orders
   */
  @GetMapping("/metrics/{metricId}/stats")
  public ResponseEntity<GlobalMetricStatsResponse> getGlobalMetricStats(@PathVariable String metricId) {
    log.debug("Getting global stats for metric ID: {}", metricId);

    List<GithubRepositoryOrderRatingEntity> ratings = ratingsService.getRatingsByMetricId(metricId);
    RatingsService.MetricStatistics stats = ratingsService.getMetricStatistics(metricId);

    GithubRepositoryOrderRatingEntity firstRating = ratings.get(0);

    GlobalMetricStatsResponse response = new GlobalMetricStatsResponse();
    response.setMetricId(metricId);
    response.setMetricName(firstRating.getMetricName());
    response.setMetricVersion(firstRating.getMetricVersion());
    response.setTotalRatings(stats.getTotalRatings());
    response.setRatingsWithRdfData(stats.getRatingsWithRdf());
    response.setUniqueOrders(stats.getUniqueOrders());

    if (!ratings.isEmpty()) {
      response.setEarliestRating(ratings.stream()
          .map(GithubRepositoryOrderRatingEntity::getCreatedAt)
          .min(LocalDateTime::compareTo).orElse(null));
      response.setLatestRating(ratings.stream()
          .map(GithubRepositoryOrderRatingEntity::getCreatedAt)
          .max(LocalDateTime::compareTo).orElse(null));
    }

    // Group by task session
    Map<String, Long> sessionStats = ratings.stream()
        .filter(r -> r.getTaskSessionId() != null)
        .collect(Collectors.groupingBy(
            GithubRepositoryOrderRatingEntity::getTaskSessionId,
            Collectors.counting()));
    response.setTaskSessions(sessionStats);

    // Group by order for breakdown
    Map<Long, OrderStats> orderStats = ratings.stream()
        .collect(Collectors.groupingBy(r -> r.getGithubRepositoryOrder().getId()))
        .entrySet().stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> createOrderStats(entry.getValue())));
    response.setOrderBreakdown(orderStats);

    return ResponseEntity.ok(response);
  }

  // ============== HELPER METHODS ==============

  private MetricSummary createMetricSummary(String metricId, List<GithubRepositoryOrderRatingEntity> ratings) {
    GithubRepositoryOrderRatingEntity firstRating = ratings.get(0);

    MetricSummary summary = new MetricSummary();
    summary.setMetricId(metricId);
    summary.setMetricName(firstRating.getMetricName());
    summary.setMetricVersion(firstRating.getMetricVersion());
    summary.setRatingCount(ratings.size());
    summary.setLatestCreated(ratings.stream()
        .map(GithubRepositoryOrderRatingEntity::getCreatedAt)
        .max(LocalDateTime::compareTo).orElse(null));
    summary.setHasRdfData(ratings.stream()
        .anyMatch(r -> r.getRdfBlob() != null));

    return summary;
  }

  private MetricStats createMetricStats(List<GithubRepositoryOrderRatingEntity> ratings) {
    MetricStats stats = new MetricStats();
    stats.setRatingCount(ratings.size());
    stats.setWithRdfData((int) ratings.stream().filter(r -> r.getRdfBlob() != null).count());
    stats.setEarliestRating(ratings.stream()
        .map(GithubRepositoryOrderRatingEntity::getCreatedAt)
        .min(LocalDateTime::compareTo).orElse(null));
    stats.setLatestRating(ratings.stream()
        .map(GithubRepositoryOrderRatingEntity::getCreatedAt)
        .max(LocalDateTime::compareTo).orElse(null));

    return stats;
  }

  private OrderMetricInfo createOrderMetricInfo(Long orderId, List<GithubRepositoryOrderRatingEntity> ratings) {
    GithubRepositoryOrderEntity order = ratings.get(0).getGithubRepositoryOrder();

    OrderMetricInfo info = new OrderMetricInfo();
    info.setOrderId(orderId);
    info.setOwner(order.getOwnerName());
    info.setRepository(order.getRepositoryName());
    info.setRatingCount(ratings.size());
    info.setHasRdfData(ratings.stream().anyMatch(r -> r.getRdfBlob() != null));
    info.setLatestRating(ratings.stream()
        .map(GithubRepositoryOrderRatingEntity::getCreatedAt)
        .max(LocalDateTime::compareTo).orElse(null));

    return info;
  }

  private OrderStats createOrderStats(List<GithubRepositoryOrderRatingEntity> ratings) {
    GithubRepositoryOrderEntity order = ratings.get(0).getGithubRepositoryOrder();

    OrderStats stats = new OrderStats();
    stats.setOwner(order.getOwnerName());
    stats.setRepository(order.getRepositoryName());
    stats.setRatingCount(ratings.size());
    stats.setWithRdfData((int) ratings.stream().filter(r -> r.getRdfBlob() != null).count());
    stats.setEarliestRating(ratings.stream()
        .map(GithubRepositoryOrderRatingEntity::getCreatedAt)
        .min(LocalDateTime::compareTo).orElse(null));
    stats.setLatestRating(ratings.stream()
        .map(GithubRepositoryOrderRatingEntity::getCreatedAt)
        .max(LocalDateTime::compareTo).orElse(null));

    return stats;
  }

  private RatingDetail convertToRatingDetail(GithubRepositoryOrderRatingEntity rating) {
    RatingDetail detail = new RatingDetail();
    detail.setId(rating.getId());
    detail.setMetricId(rating.getMetricId());
    detail.setMetricName(rating.getMetricName());
    detail.setMetricVersion(rating.getMetricVersion());
    detail.setTaskSessionId(rating.getTaskSessionId());
    detail.setCreatedAt(rating.getCreatedAt());
    detail.setHasRdfData(rating.getRdfBlob() != null);

    // Add filter info if available
    if (rating.getGithubRatingFilter() != null) {
      detail.setFilters(convertFilterToMap(rating.getGithubRatingFilter()));
    }

    return detail;
  }

  private Map<String, Object> convertFilterToMap(
      de.leipzig.htwk.gitrdf.database.common.entity.GithubRatingFilter filter) {
    Map<String, Object> filterMap = new HashMap<>();

    // Add all the boolean flags from the filter
    filterMap.put("enableMetricDefinition", filter.isEnableMetricDefinition());
    filterMap.put("enableIndividualRatings", filter.isEnableIndividualRatings());
    filterMap.put("enableAggregatedScores", filter.isEnableAggregatedScores());
    filterMap.put("enableModelMetadata", filter.isEnableModelMetadata());
    filterMap.put("enableTaskMetadata", filter.isEnableTaskMetadata());

    return filterMap;
  }
}





