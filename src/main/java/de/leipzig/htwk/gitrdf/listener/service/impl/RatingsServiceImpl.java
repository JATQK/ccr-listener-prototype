package de.leipzig.htwk.gitrdf.listener.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.leipzig.htwk.gitrdf.database.common.entity.GithubRepositoryOrderEntity;
import de.leipzig.htwk.gitrdf.database.common.entity.GithubRepositoryOrderAnalysisEntity;
import de.leipzig.htwk.gitrdf.database.common.entity.enums.AnalysisType;
import de.leipzig.htwk.gitrdf.database.common.repository.GithubRepositoryOrderAnalysisRepository;
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
  private GithubRepositoryOrderAnalysisRepository analysisRepository;

  // ============== ORDER-BASED OPERATIONS ==============

  @Override
  public GithubRepositoryOrderEntity getOrderById(Long id) {
    log.debug("Getting order by ID: {}", id);
    return orderRepository.findById(id)
        .orElseThrow(() -> RatingsNotFoundException.orderNotFound(id));
  }

  @Override
  public List<GithubRepositoryOrderAnalysisEntity> getAnalysesByOrderId(Long orderId) {
    log.debug("Getting all analyses for order ID: {}", orderId);
    getOrderById(orderId); // Verify order exists
    return analysisRepository.findAllByGithubRepositoryOrderId(orderId);
  }

  @Override
  public List<GithubRepositoryOrderAnalysisEntity> getRatingsByOrderId(Long orderId) {
    log.debug("Getting ratings for order ID: {}", orderId);
    getOrderById(orderId); // Verify order exists
    List<GithubRepositoryOrderAnalysisEntity> ratings = analysisRepository.findAllByGithubRepositoryOrderIdAndAnalysisType(orderId, AnalysisType.RATING);
    log.debug("Found {} ratings for order ID: {}", ratings.size(), orderId);
    return ratings;
  }

  @Override
  public List<GithubRepositoryOrderAnalysisEntity> getStatisticsByOrderId(Long orderId) {
    log.debug("Getting statistics for order ID: {}", orderId);
    getOrderById(orderId); // Verify order exists
    List<GithubRepositoryOrderAnalysisEntity> statistics = analysisRepository.findAllByGithubRepositoryOrderIdAndAnalysisType(orderId, AnalysisType.STATISTIC);
    log.debug("Found {} statistics for order ID: {}", statistics.size(), orderId);
    return statistics;
  }

  @Override
  public List<GithubRepositoryOrderAnalysisEntity> getExpertsByOrderId(Long orderId) {
    log.debug("Getting experts for order ID: {}", orderId);
    getOrderById(orderId); // Verify order exists
    List<GithubRepositoryOrderAnalysisEntity> experts = analysisRepository.findAllByGithubRepositoryOrderIdAndAnalysisType(orderId, AnalysisType.EXPERT);
    log.debug("Found {} experts for order ID: {}", experts.size(), orderId);
    return experts;
  }

  @Override
  public List<GithubRepositoryOrderAnalysisEntity> getAnalysesByOrderIdAndMetricId(Long orderId, String metricId) {
    log.debug("Getting all analyses for order ID: {} and metric ID: {}", orderId, metricId);
    getOrderById(orderId); // Verify order exists
    return analysisRepository.findByGithubRepositoryOrderIdAndMetricId(orderId, metricId)
        .map(List::of)
        .orElse(List.of());
  }

  @Override
  public List<GithubRepositoryOrderAnalysisEntity> getRatingsByOrderIdAndMetricId(Long orderId, String metricId) {
    log.debug("Getting ratings for order ID: {} and metric ID: {}", orderId, metricId);
    getOrderById(orderId); // Verify order exists
    return analysisRepository.findByGithubRepositoryOrderIdAndMetricId(orderId, metricId)
        .filter(analysis -> analysis.getAnalysisType() == AnalysisType.RATING)
        .map(List::of)
        .orElseThrow(() -> RatingsNotFoundException.orderMetricNotFound(orderId, metricId));
  }

  @Override
  public List<GithubRepositoryOrderAnalysisEntity> getStatisticsByOrderIdAndMetricId(Long orderId, String metricId) {
    log.debug("Getting statistics for order ID: {} and metric ID: {}", orderId, metricId);
    getOrderById(orderId); // Verify order exists
    return analysisRepository.findByGithubRepositoryOrderIdAndMetricId(orderId, metricId)
        .filter(analysis -> analysis.getAnalysisType() == AnalysisType.STATISTIC)
        .map(List::of)
        .orElse(List.of());
  }

  @Override
  public List<GithubRepositoryOrderAnalysisEntity> getAnalysesWithRdfByOrderId(Long orderId) {
    log.debug("Getting all analyses with RDF for order ID: {}", orderId);
    getOrderById(orderId); // Verify order exists
    return analysisRepository.findAllByGithubRepositoryOrderId(orderId).stream()
        .filter(analysis -> analysis.getRdfBlob() != null)
        .collect(Collectors.toList());
  }

  @Override
  public List<GithubRepositoryOrderAnalysisEntity> getRatingsWithRdfByOrderId(Long orderId) {
    log.debug("Getting ratings with RDF for order ID: {}", orderId);
    getOrderById(orderId); // Verify order exists
    List<GithubRepositoryOrderAnalysisEntity> ratings = analysisRepository.findAllByGithubRepositoryOrderIdAndAnalysisType(orderId, AnalysisType.RATING).stream()
        .filter(analysis -> analysis.getRdfBlob() != null)
        .collect(Collectors.toList());
    log.debug("Found {} ratings with RDF data for order ID: {}", ratings.size(), orderId);
    return ratings;
  }

  @Override
  public List<GithubRepositoryOrderAnalysisEntity> getStatisticsWithRdfByOrderId(Long orderId) {
    log.debug("Getting statistics with RDF for order ID: {}", orderId);
    getOrderById(orderId); // Verify order exists
    List<GithubRepositoryOrderAnalysisEntity> statistics = analysisRepository.findAllByGithubRepositoryOrderIdAndAnalysisType(orderId, AnalysisType.STATISTIC).stream()
        .filter(analysis -> analysis.getRdfBlob() != null)
        .collect(Collectors.toList());
    log.debug("Found {} statistics with RDF data for order ID: {}", statistics.size(), orderId);
    return statistics;
  }

  @Override
  public List<GithubRepositoryOrderAnalysisEntity> getExpertsWithRdfByOrderId(Long orderId) {
    log.debug("Getting experts with RDF for order ID: {}", orderId);
    getOrderById(orderId); // Verify order exists
    List<GithubRepositoryOrderAnalysisEntity> experts = analysisRepository.findAllByGithubRepositoryOrderIdAndAnalysisType(orderId, AnalysisType.EXPERT).stream()
        .filter(analysis -> analysis.getRdfBlob() != null)
        .collect(Collectors.toList());
    log.debug("Found {} experts with RDF data for order ID: {}", experts.size(), orderId);
    return experts;
  }

  @Override
  public List<GithubRepositoryOrderAnalysisEntity> getAnalysesWithRdfByOrderIdAndMetricId(Long orderId, String metricId) {
    log.debug("Getting all analyses with RDF for order ID: {} and metric ID: {}", orderId, metricId);
    getOrderById(orderId); // Verify order exists
    return analysisRepository.findByGithubRepositoryOrderIdAndMetricId(orderId, metricId)
        .filter(analysis -> analysis.getRdfBlob() != null)
        .map(List::of)
        .orElse(List.of());
  }

  @Override
  public List<GithubRepositoryOrderAnalysisEntity> getRatingsWithRdfByOrderIdAndMetricId(Long orderId, String metricId) {
    log.debug("Getting ratings with RDF for order ID: {} and metric ID: {}", orderId, metricId);
    getOrderById(orderId); // Verify order exists
    return analysisRepository.findByGithubRepositoryOrderIdAndMetricId(orderId, metricId)
        .filter(analysis -> analysis.getAnalysisType() == AnalysisType.RATING && analysis.getRdfBlob() != null)
        .map(List::of)
        .orElseThrow(() -> RatingsNotFoundException.noRdfDataFound("order: " + orderId + ", metric: " + metricId));
  }

  @Override
  public List<GithubRepositoryOrderAnalysisEntity> getStatisticsWithRdfByOrderIdAndMetricId(Long orderId, String metricId) {
    log.debug("Getting statistics with RDF for order ID: {} and metric ID: {}", orderId, metricId);
    getOrderById(orderId); // Verify order exists
    return analysisRepository.findByGithubRepositoryOrderIdAndMetricId(orderId, metricId)
        .filter(analysis -> analysis.getAnalysisType() == AnalysisType.STATISTIC && analysis.getRdfBlob() != null)
        .map(List::of)
        .orElse(List.of());
  }

  // ============== METRIC-BASED OPERATIONS ==============

  @Override
  public List<GithubRepositoryOrderAnalysisEntity> getAnalysesByMetricId(String metricId) {
    log.debug("Getting all analyses for metric ID: {}", metricId);
    List<GithubRepositoryOrderAnalysisEntity> analyses = analysisRepository.findAllByGithubRepositoryOrderId(null).stream()
        .filter(analysis -> metricId.equals(analysis.getMetricId()))
        .collect(Collectors.toList());
    log.debug("Found {} analyses for metric ID: {}", analyses.size(), metricId);
    return analyses;
  }

  @Override
  public List<GithubRepositoryOrderAnalysisEntity> getRatingsByMetricId(String metricId) {
    log.debug("Getting ratings for metric ID: {}", metricId);
    // Note: We need a different approach since we don't have a direct findByMetricId method
    // This is a simplified implementation - you might need to add a custom query method
    List<GithubRepositoryOrderAnalysisEntity> ratings = getAllAnalyses().stream()
        .filter(analysis -> metricId.equals(analysis.getMetricId()) && analysis.getAnalysisType() == AnalysisType.RATING)
        .collect(Collectors.toList());
    if (ratings.isEmpty()) {
      throw RatingsNotFoundException.metricNotFound(metricId);
    }
    log.debug("Found {} ratings for metric ID: {}", ratings.size(), metricId);
    return ratings;
  }

  @Override
  public List<GithubRepositoryOrderAnalysisEntity> getStatisticsByMetricId(String metricId) {
    log.debug("Getting statistics for metric ID: {}", metricId);
    List<GithubRepositoryOrderAnalysisEntity> statistics = getAllAnalyses().stream()
        .filter(analysis -> metricId.equals(analysis.getMetricId()) && analysis.getAnalysisType() == AnalysisType.STATISTIC)
        .collect(Collectors.toList());
    log.debug("Found {} statistics for metric ID: {}", statistics.size(), metricId);
    return statistics;
  }

  @Override
  public List<GithubRepositoryOrderAnalysisEntity> getAnalysesWithRdfByMetricId(String metricId) {
    log.debug("Getting analyses with RDF for metric ID: {}", metricId);
    List<GithubRepositoryOrderAnalysisEntity> analyses = getAllAnalyses().stream()
        .filter(analysis -> metricId.equals(analysis.getMetricId()) && analysis.getRdfBlob() != null)
        .collect(Collectors.toList());
    log.debug("Found {} analyses with RDF data for metric ID: {}", analyses.size(), metricId);
    return analyses;
  }

  @Override
  public List<GithubRepositoryOrderAnalysisEntity> getRatingsWithRdfByMetricId(String metricId) {
    log.debug("Getting ratings with RDF for metric ID: {}", metricId);
    List<GithubRepositoryOrderAnalysisEntity> ratings = getAllAnalyses().stream()
        .filter(analysis -> metricId.equals(analysis.getMetricId()) 
            && analysis.getAnalysisType() == AnalysisType.RATING 
            && analysis.getRdfBlob() != null)
        .collect(Collectors.toList());
    if (ratings.isEmpty()) {
      throw RatingsNotFoundException.noRdfDataFound("metric: " + metricId);
    }
    log.debug("Found {} ratings with RDF data for metric ID: {}", ratings.size(), metricId);
    return ratings;
  }

  @Override
  public List<GithubRepositoryOrderAnalysisEntity> getStatisticsWithRdfByMetricId(String metricId) {
    log.debug("Getting statistics with RDF for metric ID: {}", metricId);
    List<GithubRepositoryOrderAnalysisEntity> statistics = getAllAnalyses().stream()
        .filter(analysis -> metricId.equals(analysis.getMetricId()) 
            && analysis.getAnalysisType() == AnalysisType.STATISTIC 
            && analysis.getRdfBlob() != null)
        .collect(Collectors.toList());
    log.debug("Found {} statistics with RDF data for metric ID: {}", statistics.size(), metricId);
    return statistics;
  }

  // ============== DOWNLOAD OPERATIONS ==============

  @Override
  @Transactional(readOnly = true)
  public RdfDownloadResult createRdfDownload(List<GithubRepositoryOrderAnalysisEntity> analyses, String baseFilename)
      throws SQLException, IOException {
    log.debug("Creating RDF download for {} analyses with base filename: {}", analyses.size(), baseFilename);

    if (analyses.isEmpty()) {
      log.warn("No analyses provided for RDF download");
      return new RdfDownloadResult(null, 0);
    }

    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      int fileCount = 0;

      for (int i = 0; i < analyses.size(); i++) {
        GithubRepositoryOrderAnalysisEntity analysis = analyses.get(i);
        Blob rdfBlob = analysis.getRdfBlob();

        if (rdfBlob != null) {
          // Add filename separator
          String separator = createFilenameSeparator(analysis);
          outputStream.write(separator.getBytes());

          // Add RDF content
          byte[] rdfData = rdfBlob.getBytes(1, (int) rdfBlob.length());
          outputStream.write(rdfData);

          // Add newline between files (except for the last one)
          if (i < analyses.size() - 1) {
            outputStream.write("\n".getBytes());
          }

          fileCount++;
        }
      }

      if (fileCount == 0) {
        log.warn("No RDF data found in provided analyses");
        return new RdfDownloadResult(null, 0);
      }

      ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());
      log.debug("Created RDF download with {} files", fileCount);
      return new RdfDownloadResult(resource, fileCount);
    }
  }

  @Override
  public String generateDownloadFilename(String type, Long orderId, String metricId, GithubRepositoryOrderEntity order) {
    StringBuilder filename = new StringBuilder();
    filename.append(type);

    if (orderId != null) {
      filename.append("_order_").append(orderId);
    }

    if (order != null) {
      filename.append("_").append(order.getOwnerName())
              .append("_").append(order.getRepositoryName());
    }

    if (metricId != null) {
      filename.append("_metric_").append(metricId);
    }

    filename.append(".ttl");
    return filename.toString();
  }

  // ============== STATISTICS OPERATIONS ==============

  @Override
  public OrderStatistics getOrderStatistics(Long orderId) {
    log.debug("Getting statistics for order ID: {}", orderId);
    
    Long totalRatings = analysisRepository.countByGithubRepositoryOrderIdAndAnalysisType(orderId, AnalysisType.RATING);
    Long totalStatistics = analysisRepository.countByGithubRepositoryOrderIdAndAnalysisType(orderId, AnalysisType.STATISTIC);
    Long totalExperts = analysisRepository.countByGithubRepositoryOrderIdAndAnalysisType(orderId, AnalysisType.EXPERT);
    
    List<GithubRepositoryOrderAnalysisEntity> allAnalyses = analysisRepository.findAllByGithubRepositoryOrderId(orderId);
    Long uniqueMetrics = allAnalyses.stream()
        .map(GithubRepositoryOrderAnalysisEntity::getMetricId)
        .distinct()
        .count();
    
    Long ratingsWithRdf = allAnalyses.stream()
        .filter(analysis -> analysis.getAnalysisType() == AnalysisType.RATING && analysis.getRdfBlob() != null)
        .count();
    
    Long statisticsWithRdf = allAnalyses.stream()
        .filter(analysis -> analysis.getAnalysisType() == AnalysisType.STATISTIC && analysis.getRdfBlob() != null)
        .count();

    Long expertsWithRdf = allAnalyses.stream()
        .filter(analysis -> analysis.getAnalysisType() == AnalysisType.EXPERT && analysis.getRdfBlob() != null)
        .count();

    OrderStatistics stats = new OrderStatistics(
        totalRatings.intValue(), 
        totalStatistics.intValue(), 
        totalExperts.intValue(),
        uniqueMetrics.intValue(),
        ratingsWithRdf.intValue(), 
        statisticsWithRdf.intValue(),
        expertsWithRdf.intValue()
    );
    
    log.debug("Order {} statistics: {} ratings, {} statistics, {} experts, {} unique metrics, {} ratings with RDF, {} statistics with RDF, {} experts with RDF", 
        orderId, stats.getTotalRatings(), stats.getTotalStatistics(), stats.getTotalExperts(), stats.getUniqueMetrics(), 
        stats.getRatingsWithRdf(), stats.getStatisticsWithRdf(), stats.getExpertsWithRdf());
    return stats;
  }

  @Override
  public MetricStatistics getMetricStatistics(String metricId) {
    log.debug("Getting statistics for metric ID: {}", metricId);
    
    List<GithubRepositoryOrderAnalysisEntity> allAnalyses = getAllAnalyses();
    
    Long totalRatings = allAnalyses.stream()
        .filter(analysis -> metricId.equals(analysis.getMetricId()) && analysis.getAnalysisType() == AnalysisType.RATING)
        .count();
    
    Long totalStatistics = allAnalyses.stream()
        .filter(analysis -> metricId.equals(analysis.getMetricId()) && analysis.getAnalysisType() == AnalysisType.STATISTIC)
        .count();
    
    Long uniqueOrders = allAnalyses.stream()
        .filter(analysis -> metricId.equals(analysis.getMetricId()))
        .map(analysis -> analysis.getGithubRepositoryOrder().getId())
        .distinct()
        .count();
    
    Long ratingsWithRdf = allAnalyses.stream()
        .filter(analysis -> metricId.equals(analysis.getMetricId()) 
            && analysis.getAnalysisType() == AnalysisType.RATING 
            && analysis.getRdfBlob() != null)
        .count();
    
    Long statisticsWithRdf = allAnalyses.stream()
        .filter(analysis -> metricId.equals(analysis.getMetricId()) 
            && analysis.getAnalysisType() == AnalysisType.STATISTIC 
            && analysis.getRdfBlob() != null)
        .count();

    MetricStatistics stats = new MetricStatistics(
        totalRatings.intValue(), 
        totalStatistics.intValue(), 
        uniqueOrders.intValue(),
        ratingsWithRdf.intValue(), 
        statisticsWithRdf.intValue()
    );
    
    log.debug("Metric {} statistics: {} ratings, {} statistics, {} unique orders, {} ratings with RDF, {} statistics with RDF", 
        metricId, stats.getTotalRatings(), stats.getTotalStatistics(), stats.getUniqueOrders(), 
        stats.getRatingsWithRdf(), stats.getStatisticsWithRdf());
    return stats;
  }

  // ============== UTILITY OPERATIONS ==============

  @Override
  public List<String> getAllMetricIds() {
    return getAllAnalyses().stream()
        .map(GithubRepositoryOrderAnalysisEntity::getMetricId)
        .distinct()
        .collect(Collectors.toList());
  }

  @Override
  public List<String> getMetricIdsByOrderId(Long orderId) {
    return analysisRepository.findAllByGithubRepositoryOrderId(orderId).stream()
        .map(GithubRepositoryOrderAnalysisEntity::getMetricId)
        .distinct()
        .collect(Collectors.toList());
  }

  @Override
  public List<Long> getOrderIdsByMetricId(String metricId) {
    return getAllAnalyses().stream()
        .filter(analysis -> metricId.equals(analysis.getMetricId()))
        .map(analysis -> analysis.getGithubRepositoryOrder().getId())
        .distinct()
        .collect(Collectors.toList());
  }

  @Override
  public List<Long> getOrderIdsWithExperts() {
    log.debug("Getting all order IDs that have expert analyses");
    return getAllAnalyses().stream()
        .filter(analysis -> analysis.getAnalysisType() == AnalysisType.EXPERT)
        .map(analysis -> analysis.getGithubRepositoryOrder().getId())
        .distinct()
        .collect(Collectors.toList());
  }

  // ============== PRIVATE HELPER METHODS ==============

  private List<GithubRepositoryOrderAnalysisEntity> getAllAnalyses() {
    // This is a helper method to get all analyses - you might want to implement pagination for large datasets
    return orderRepository.findAll().stream()
        .flatMap(order -> analysisRepository.findAllByGithubRepositoryOrderId(order.getId()).stream())
        .collect(Collectors.toList());
  }

  private String createFilenameSeparator(GithubRepositoryOrderAnalysisEntity analysis) {
    GithubRepositoryOrderEntity order = analysis.getGithubRepositoryOrder();

    if (order != null) {
      return String.format("##filename: order_%d_%s_%s_%s_%s.ttl\n",
          order.getId(),
          order.getOwnerName(),
          order.getRepositoryName(),
          analysis.getMetricId(),
          analysis.getAnalysisType().toString().toLowerCase());
    } else {
      return String.format("##filename: %s_%s_%s.ttl\n",
          analysis.getMetricId(),
          analysis.getId(),
          analysis.getAnalysisType().toString().toLowerCase());
    }
  }
}