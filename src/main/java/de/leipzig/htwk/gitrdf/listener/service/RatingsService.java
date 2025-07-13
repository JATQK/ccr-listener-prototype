package de.leipzig.htwk.gitrdf.listener.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.core.io.Resource;

import de.leipzig.htwk.gitrdf.database.common.entity.GithubRepositoryOrderEntity;
import de.leipzig.htwk.gitrdf.database.common.entity.GithubRepositoryOrderRatingEntity;

public interface RatingsService {

  // ============== ORDER-BASED OPERATIONS ==============

  /**
   * Get order by ID or throw exception
   */
  GithubRepositoryOrderEntity getOrderById(Long id);

  /**
   * Get all ratings for an order by ID
   */
  List<GithubRepositoryOrderRatingEntity> getRatingsByOrderId(Long orderId);

  /**
   * Get ratings for an order and specific metric
   */
  List<GithubRepositoryOrderRatingEntity> getRatingsByOrderIdAndMetricId(Long orderId, String metricId);

  /**
   * Get ratings with RDF data for an order
   */
  List<GithubRepositoryOrderRatingEntity> getRatingsWithRdfByOrderId(Long orderId);

  /**
   * Get ratings with RDF data for an order and specific metric
   */
  List<GithubRepositoryOrderRatingEntity> getRatingsWithRdfByOrderIdAndMetricId(Long orderId, String metricId);

  // ============== METRIC-BASED OPERATIONS ==============

  /**
   * Get all ratings for a specific metric across all orders
   */
  List<GithubRepositoryOrderRatingEntity> getRatingsByMetricId(String metricId);

  /**
   * Get ratings with RDF data for a specific metric across all orders
   */
  List<GithubRepositoryOrderRatingEntity> getRatingsWithRdfByMetricId(String metricId);

  // ============== DOWNLOAD OPERATIONS ==============

  /**
   * Create downloadable RDF resource for multiple ratings
   */
  RdfDownloadResult createRdfDownload(List<GithubRepositoryOrderRatingEntity> ratings, String baseFilename)
      throws SQLException, IOException;

  /**
   * Generate appropriate filename for download
   */
  String generateDownloadFilename(String type, Long orderId, String metricId, GithubRepositoryOrderEntity order);

  // ============== STATISTICS OPERATIONS ==============

  /**
   * Count statistics for an order
   */
  OrderStatistics getOrderStatistics(Long orderId);

  /**
   * Count statistics for a metric
   */
  MetricStatistics getMetricStatistics(String metricId);

  // ============== UTILITY OPERATIONS ==============

  /**
   * Get all distinct metric IDs
   */
  List<String> getAllMetricIds();

  /**
   * Get distinct metric IDs for an order
   */
  List<String> getMetricIdsByOrderId(Long orderId);

  /**
   * Get distinct order IDs for a metric
   */
  List<Long> getOrderIdsByMetricId(String metricId);

  // ============== RESULT CLASSES ==============

  /**
   * Result class for RDF download operations
   */
  public static class RdfDownloadResult {
    private final Resource resource;
    private final int fileCount;

    public RdfDownloadResult(Resource resource, int fileCount) {
      this.resource = resource;
      this.fileCount = fileCount;
    }

    public Resource getResource() {
      return resource;
    }

    public int getFileCount() {
      return fileCount;
    }

    public boolean hasContent() {
      return resource != null && fileCount > 0;
    }
  }

  /**
   * Statistics for an order
   */
  public static class OrderStatistics {
    private final int totalRatings;
    private final int uniqueMetrics;
    private final int ratingsWithRdf;

    public OrderStatistics(int totalRatings, int uniqueMetrics, int ratingsWithRdf) {
      this.totalRatings = totalRatings;
      this.uniqueMetrics = uniqueMetrics;
      this.ratingsWithRdf = ratingsWithRdf;
    }

    public int getTotalRatings() {
      return totalRatings;
    }

    public int getUniqueMetrics() {
      return uniqueMetrics;
    }

    public int getRatingsWithRdf() {
      return ratingsWithRdf;
    }
  }

  /**
   * Statistics for a metric
   */
  public static class MetricStatistics {
    private final int totalRatings;
    private final int uniqueOrders;
    private final int ratingsWithRdf;

    public MetricStatistics(int totalRatings, int uniqueOrders, int ratingsWithRdf) {
      this.totalRatings = totalRatings;
      this.uniqueOrders = uniqueOrders;
      this.ratingsWithRdf = ratingsWithRdf;
    }

    public int getTotalRatings() {
      return totalRatings;
    }

    public int getUniqueOrders() {
      return uniqueOrders;
    }

    public int getRatingsWithRdf() {
      return ratingsWithRdf;
    }
  }
}