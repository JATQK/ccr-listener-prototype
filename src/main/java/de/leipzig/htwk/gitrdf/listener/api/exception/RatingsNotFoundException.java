package de.leipzig.htwk.gitrdf.listener.api.exception;

import lombok.Getter;

@Getter
public class RatingsNotFoundException extends RuntimeException {

  public static RatingsNotFoundException orderNotFound(long id) {
    String status = "Not found";
    String reason = String.format("No repository order found for id '%d'", id);
    String solution = "Provide an id for an existing repository order";

    String message = getMessageFrom(status, reason, solution);
    return new RatingsNotFoundException(message, status, reason, solution);
  }

  public static RatingsNotFoundException metricNotFound(String metricId) {
    String status = "Not found";
    String reason = String.format("No ratings found for metric '%s'", metricId);
    String solution = "Provide a metric ID that has ratings in the system";

    String message = getMessageFrom(status, reason, solution);
    return new RatingsNotFoundException(message, status, reason, solution);
  }

  public static RatingsNotFoundException orderMetricNotFound(long orderId, String metricId) {
    String status = "Not found";
    String reason = String.format("No ratings found for order '%d' and metric '%s'", orderId, metricId);
    String solution = "Provide valid order ID and metric ID combination that exists in the system";

    String message = getMessageFrom(status, reason, solution);
    return new RatingsNotFoundException(message, status, reason, solution);
  }

  public static RatingsNotFoundException noRdfDataFound(String context) {
    String status = "Not found";
    String reason = String.format("No RDF data available for %s", context);
    String solution = "Ensure the ratings have been processed and contain RDF data";

    String message = getMessageFrom(status, reason, solution);
    return new RatingsNotFoundException(message, status, reason, solution);
  }

  private final String status;
  private final String reason;
  private final String solution;

  private RatingsNotFoundException(String message, String status, String reason, String solution) {
    super(message);
    this.status = status;
    this.reason = reason;
    this.solution = solution;
  }

  private static String getMessageFrom(String status, String reason, String solution) {
    return String.format("Status: %s, Reason: %s, Solution: %s", status, reason, solution);
  }
}