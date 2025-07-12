package de.leipzig.htwk.gitrdf.listener.service;

import de.leipzig.htwk.gitrdf.database.common.entity.GithubRepositoryOrderMetricRatingEntity;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface GithubRatingService {

    List<GithubRepositoryOrderMetricRatingEntity> findByGithubRepositoryOrderId(long orderId);

    File getTempRdfFile(long ratingId) throws SQLException, IOException;
}
