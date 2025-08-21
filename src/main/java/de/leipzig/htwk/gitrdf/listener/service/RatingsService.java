package de.leipzig.htwk.gitrdf.listener.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.core.io.Resource;

import de.leipzig.htwk.gitrdf.database.common.entity.GithubRepositoryOrderEntity;
import de.leipzig.htwk.gitrdf.database.common.entity.GithubRepositoryOrderAnalysisEntity;
import de.leipzig.htwk.gitrdf.database.common.entity.enums.AnalysisType;

public interface RatingsService {

  // ============== ORDER-BASED OPERATIONS ==============

  /**
   * Get order by ID or throw exception
   */
  GithubRepositoryOrderEntity getOrderById(Long id);

  /**
   * Get all analyses for an order by ID
   */
  List<GithubRepositoryOrderAnalysisEntity> getAnalysesByOrderId(Long orderId);

  /**
   * Get all ratings for an order by ID
   */
  List<GithubRepositoryOrderAnalysisEntity> getRatingsByOrderId(Long orderId);

  /**
   * Get all statistics for an order by ID
   */
  List<GithubRepositoryOrderAnalysisEntity> getStatisticsByOrderId(Long orderId);

  /**
   * Get all experts for an order by ID
   */
  List<GithubRepositoryOrderAnalysisEntity> getExpertsByOrderId(Long orderId);

  /**
   * Get analyses for an order and specific metric
   */
  List<GithubRepositoryOrderAnalysisEntity> getAnalysesByOrderIdAndMetricId(Long orderId, String metricId);

  /**
   * Get ratings for an order and specific metric
   */
  List<GithubRepositoryOrderAnalysisEntity> getRatingsByOrderIdAndMetricId(Long orderId, String metricId);

  /**
   * Get statistics for an order and specific metric
   */
  List<GithubRepositoryOrderAnalysisEntity> getStatisticsByOrderIdAndMetricId(Long orderId, String metricId);

  /**
   * Get analyses with RDF data for an order
   */
  List<GithubRepositoryOrderAnalysisEntity> getAnalysesWithRdfByOrderId(Long orderId);

  /**
   * Get ratings with RDF data for an order
   */
  List<GithubRepositoryOrderAnalysisEntity> getRatingsWithRdfByOrderId(Long orderId);

  /**
   * Get statistics with RDF data for an order
   */
  List<GithubRepositoryOrderAnalysisEntity> getStatisticsWithRdfByOrderId(Long orderId);

  /**
   * Get experts with RDF data for an order
   */
  List<GithubRepositoryOrderAnalysisEntity> getExpertsWithRdfByOrderId(Long orderId);

  /**
   * Get analyses with RDF data for an order and specific metric
   */
  List<GithubRepositoryOrderAnalysisEntity> getAnalysesWithRdfByOrderIdAndMetricId(Long orderId, String metricId);

  /**
   * Get ratings with RDF data for an order and specific metric
   */
  List<GithubRepositoryOrderAnalysisEntity> getRatingsWithRdfByOrderIdAndMetricId(Long orderId, String metricId);

  /**
   * Get statistics with RDF data for an order and specific metric
   */
  List<GithubRepositoryOrderAnalysisEntity> getStatisticsWithRdfByOrderIdAndMetricId(Long orderId, String metricId);

  // ============== METRIC-BASED OPERATIONS ==============

  /**
   * Get all analyses for a specific metric across all orders
   */
  List<GithubRepositoryOrderAnalysisEntity> getAnalysesByMetricId(String metricId);

  /**
   * Get all ratings for a specific metric across all orders
   */
  List<GithubRepositoryOrderAnalysisEntity> getRatingsByMetricId(String metricId);

  /**
   * Get all statistics for a specific metric across all orders
   */
  List<GithubRepositoryOrderAnalysisEntity> getStatisticsByMetricId(String metricId);

  /**
   * Get analyses with RDF data for a specific metric across all orders
   */
  List<GithubRepositoryOrderAnalysisEntity> getAnalysesWithRdfByMetricId(String metricId);

  /**
   * Get ratings with RDF data for a specific metric across all orders
   */
  List<GithubRepositoryOrderAnalysisEntity> getRatingsWithRdfByMetricId(String metricId);

  /**
   * Get statistics with RDF data for a specific metric across all orders
   */
  List<GithubRepositoryOrderAnalysisEntity> getStatisticsWithRdfByMetricId(String metricId);

  // ============== DOWNLOAD OPERATIONS ==============

  /**
   * Create downloadable RDF resource for multiple analyses
   */
  RdfDownloadResult createRdfDownload(List<GithubRepositoryOrderAnalysisEntity> analyses, String baseFilename)
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

  /**
   * Get all order IDs that have expert analyses
   */
  List<Long> getOrderIdsWithExperts();

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
    private final int totalStatistics;
    private final int totalExperts;
    private final int uniqueMetrics;
    private final int ratingsWithRdf;
    private final int statisticsWithRdf;
    private final int expertsWithRdf;

    public OrderStatistics(int totalRatings, int totalStatistics, int totalExperts, int uniqueMetrics, int ratingsWithRdf, int statisticsWithRdf, int expertsWithRdf) {
      this.totalRatings = totalRatings;
      this.totalStatistics = totalStatistics;
      this.totalExperts = totalExperts;
      this.uniqueMetrics = uniqueMetrics;
      this.ratingsWithRdf = ratingsWithRdf;
      this.statisticsWithRdf = statisticsWithRdf;
      this.expertsWithRdf = expertsWithRdf;
    }

    public int getTotalRatings() {
      return totalRatings;
    }

    public int getTotalStatistics() {
      return totalStatistics;
    }

    public int getTotalExperts() {
      return totalExperts;
    }

    public int getUniqueMetrics() {
      return uniqueMetrics;
    }

    public int getRatingsWithRdf() {
      return ratingsWithRdf;
    }

    public int getStatisticsWithRdf() {
      return statisticsWithRdf;
    }

    public int getExpertsWithRdf() {
      return expertsWithRdf;
    }
  }

  /**
   * Statistics for a metric
   */
  public static class MetricStatistics {
    private final int totalRatings;
    private final int totalStatistics;
    private final int uniqueOrders;
    private final int ratingsWithRdf;
    private final int statisticsWithRdf;

    public MetricStatistics(int totalRatings, int totalStatistics, int uniqueOrders, int ratingsWithRdf, int statisticsWithRdf) {
      this.totalRatings = totalRatings;
      this.totalStatistics = totalStatistics;
      this.uniqueOrders = uniqueOrders;
      this.ratingsWithRdf = ratingsWithRdf;
      this.statisticsWithRdf = statisticsWithRdf;
    }

    public int getTotalRatings() {
      return totalRatings;
    }

    public int getTotalStatistics() {
      return totalStatistics;
    }

    public int getUniqueOrders() {
      return uniqueOrders;
    }

    public int getRatingsWithRdf() {
      return ratingsWithRdf;
    }

    public int getStatisticsWithRdf() {
      return statisticsWithRdf;
    }
  }
}