package de.leipzig.htwk.gitrdf.listener.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import de.leipzig.htwk.gitrdf.database.common.entity.GithubRepositoryOrderEntity;
import de.leipzig.htwk.gitrdf.database.common.entity.GithubRepositoryOrderRatingEntity;
import de.leipzig.htwk.gitrdf.database.common.repository.GithubRepositoryOrderRatingRepository;
import de.leipzig.htwk.gitrdf.database.common.repository.GithubRepositoryOrderRepository;
import de.leipzig.htwk.gitrdf.listener.api.exception.RatingsNotFoundException;
import de.leipzig.htwk.gitrdf.listener.service.RatingsService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RatingsServiceImpl implements RatingsService {

  @Autowired
  private GithubRepositoryOrderRepository orderRepository;

  @Autowired
  private GithubRepositoryOrderRatingRepository ratingRepository;


  @Override
  public GithubRepositoryOrderEntity getOrderById(Long id) {
    log.debug("Getting order by ID: {}", id);
    return orderRepository.findById(id)
        .orElseThrow(() -> RatingsNotFoundException.orderNotFound(id));
  }

  @Override
  public List<GithubRepositoryOrderRatingEntity> getRatingsByOrderId(Long orderId) {
    log.debug("Getting ratings for order ID: {}", orderId);
    // Verify order exists
    getOrderById(orderId);
    List<GithubRepositoryOrderRatingEntity> ratings = ratingRepository.findByGithubRepositoryOrderId(orderId);
    log.debug("Found {} ratings for order ID: {}", ratings.size(), orderId);
    return ratings;
  }

  @Override
  public List<GithubRepositoryOrderRatingEntity> getRatingsByOrderIdAndMetricId(Long orderId, String metricId) {
    log.debug("Getting ratings for order ID: {} and metric ID: {}", orderId, metricId);
    // Verify order exists
    getOrderById(orderId);

    List<GithubRepositoryOrderRatingEntity> ratings = ratingRepository.findByGithubRepositoryOrderIdAndMetricId(orderId,
        metricId);
    if (ratings.isEmpty()) {
      throw RatingsNotFoundException.orderMetricNotFound(orderId, metricId);
    }
    log.debug("Found {} ratings for order ID: {} and metric ID: {}", ratings.size(), orderId, metricId);
    return ratings;
  }

  @Override
  public List<GithubRepositoryOrderRatingEntity> getRatingsWithRdfByOrderId(Long orderId) {
    log.debug("Getting ratings with RDF data for order ID: {}", orderId);
    // Verify order exists
    getOrderById(orderId);
    List<GithubRepositoryOrderRatingEntity> ratings = ratingRepository
        .findByGithubRepositoryOrderIdWithRdfData(orderId);
    log.debug("Found {} ratings with RDF data for order ID: {}", ratings.size(), orderId);
    return ratings;
  }

  @Override
  public List<GithubRepositoryOrderRatingEntity> getRatingsWithRdfByOrderIdAndMetricId(Long orderId, String metricId) {
    log.debug("Getting ratings with RDF data for order ID: {} and metric ID: {}", orderId, metricId);
    // Verify order exists
    getOrderById(orderId);
    List<GithubRepositoryOrderRatingEntity> ratings = ratingRepository
        .findByGithubRepositoryOrderIdAndMetricIdWithRdfData(orderId, metricId);
    log.debug("Found {} ratings with RDF data for order ID: {} and metric ID: {}", ratings.size(), orderId, metricId);
    return ratings;
  }

  //  METRIC-BASED OPERATIONS 

  @Override
  public List<GithubRepositoryOrderRatingEntity> getRatingsByMetricId(String metricId) {
    log.debug("Getting ratings for metric ID: {}", metricId);
    List<GithubRepositoryOrderRatingEntity> ratings = ratingRepository.findByMetricId(metricId);
    if (ratings.isEmpty()) {
      throw RatingsNotFoundException.metricNotFound(metricId);
    }
    log.debug("Found {} ratings for metric ID: {}", ratings.size(), metricId);
    return ratings;
  }

  @Override
  public List<GithubRepositoryOrderRatingEntity> getRatingsWithRdfByMetricId(String metricId) {
    log.debug("Getting ratings with RDF data for metric ID: {}", metricId);
    List<GithubRepositoryOrderRatingEntity> ratings = ratingRepository.findByMetricIdWithRdfData(metricId);
    if (ratings.isEmpty()) {
      throw RatingsNotFoundException.noRdfDataFound("metric: " + metricId);
    }
    log.debug("Found {} ratings with RDF data for metric ID: {}", ratings.size(), metricId);
    return ratings;
  }

  // ============== DOWNLOAD OPERATIONS ==============

  @Override
  public RdfDownloadResult createRdfDownload(List<GithubRepositoryOrderRatingEntity> ratings, String baseFilename)
      throws SQLException, IOException {

    log.debug("Creating RDF download for {} ratings with base filename: {}", ratings.size(), baseFilename);

    if (ratings.isEmpty()) {
      log.debug("No ratings provided, returning empty result");
      return new RdfDownloadResult(null, 0);
    }

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    int fileCount = 0;

    for (int i = 0; i < ratings.size(); i++) {
      GithubRepositoryOrderRatingEntity rating = ratings.get(i);

      if (rating.getRdfBlob() == null) {
        log.debug("Skipping rating ID {} - no RDF blob", rating.getId());
        continue;
      }

      fileCount++;

      // Add filename separator
      String separator = createFilenameSeparator(rating);
      outputStream.write(separator.getBytes());

      // Add RDF content
      Blob rdfBlob = rating.getRdfBlob();
      byte[] rdfBytes = rdfBlob.getBytes(1, (int) rdfBlob.length());
      outputStream.write(rdfBytes);

      // Add separator between files (except for last file)
      if (i < ratings.size() - 1) {
        outputStream.write("\n\n".getBytes());
      }
    }

    if (fileCount == 0) {
      log.debug("No ratings with RDF data found, returning empty result");
      return new RdfDownloadResult(null, 0);
    }

    ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
    log.debug("Created RDF download with {} files, total size: {} bytes", fileCount, outputStream.size());
    return new RdfDownloadResult(resource, fileCount);
  }

  @Override
  public String generateDownloadFilename(String type, Long orderId, String metricId,
      GithubRepositoryOrderEntity order) {
    String filename;
    switch (type) {
      case "order":
        filename = String.format("order_%d_%s_%s_rdf_export.txt",
            orderId, order.getOwnerName(), order.getRepositoryName());
        break;
      case "order_metric":
        filename = String.format("order_%d_metric_%s_rdf_export.txt", orderId, metricId);
        break;
      case "metric":
        filename = String.format("metric_%s_global_rdf_export.txt", metricId);
        break;
      default:
        filename = "rdf_export.txt";
        break;
    }
    log.debug("Generated download filename: {}", filename);
    return filename;
  }

  //  STATISTICS OPERATIONS 

  @Override
  public OrderStatistics getOrderStatistics(Long orderId) {
    log.debug("Getting statistics for order ID: {}", orderId);
    Long totalRatings = ratingRepository.countByGithubRepositoryOrderId(orderId);
    Long uniqueMetrics = ratingRepository.countUniqueMetricsByGithubRepositoryOrderId(orderId);
    Long ratingsWithRdf = ratingRepository.countByGithubRepositoryOrderIdWithRdfData(orderId);

    OrderStatistics stats = new OrderStatistics(totalRatings.intValue(), uniqueMetrics.intValue(),
        ratingsWithRdf.intValue());
    log.debug("Order {} statistics: {} total ratings, {} unique metrics, {} with RDF data",
        orderId, stats.getTotalRatings(), stats.getUniqueMetrics(), stats.getRatingsWithRdf());
    return stats;
  }

  @Override
  public MetricStatistics getMetricStatistics(String metricId) {
    log.debug("Getting statistics for metric ID: {}", metricId);
    Long totalRatings = ratingRepository.countByMetricId(metricId);
    Long uniqueOrders = ratingRepository.countUniqueOrdersByMetricId(metricId);
    Long ratingsWithRdf = ratingRepository.countByMetricIdWithRdfData(metricId);

    MetricStatistics stats = new MetricStatistics(totalRatings.intValue(), uniqueOrders.intValue(),
        ratingsWithRdf.intValue());
    log.debug("Metric {} statistics: {} total ratings, {} unique orders, {} with RDF data",
        metricId, stats.getTotalRatings(), stats.getUniqueOrders(), stats.getRatingsWithRdf());
    return stats;
  }

  //  UTILITY OPERATIONS 

  @Override
  public List<String> getAllMetricIds() {
    log.debug("Getting all distinct metric IDs");
    List<String> metricIds = ratingRepository.findAllDistinctMetricIds();
    log.debug("Found {} distinct metric IDs", metricIds.size());
    return metricIds;
  }

  @Override
  public List<String> getMetricIdsByOrderId(Long orderId) {
    log.debug("Getting distinct metric IDs for order ID: {}", orderId);
    // Verify order exists
    getOrderById(orderId);
    List<String> metricIds = ratingRepository.findDistinctMetricIdsByGithubRepositoryOrderId(orderId);
    log.debug("Found {} distinct metric IDs for order ID: {}", metricIds.size(), orderId);
    return metricIds;
  }

  @Override
  public List<Long> getOrderIdsByMetricId(String metricId) {
    log.debug("Getting distinct order IDs for metric ID: {}", metricId);
    List<Long> orderIds = ratingRepository.findDistinctOrderIdsByMetricId(metricId);
    log.debug("Found {} distinct order IDs for metric ID: {}", orderIds.size(), metricId);
    return orderIds;
  }

  //   HELPER METHODS 

  /**
   * Create filename separator for RDF export
   */
  private String createFilenameSeparator(GithubRepositoryOrderRatingEntity rating) {
    GithubRepositoryOrderEntity order = rating.getGithubRepositoryOrder();

    if (order != null) {
      return String.format("##filename: order_%d_%s_%s_%s_v%d.ttl\n",
          order.getId(),
          order.getOwnerName(),
          order.getRepositoryName(),
          rating.getMetricId(),
          rating.getMetricVersion());
    } else {
      return String.format("##filename: %s_%s_v%d.ttl\n",
          rating.getMetricId(),
          rating.getId(),
          rating.getMetricVersion());
    }
  }
}