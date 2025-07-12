package de.leipzig.htwk.gitrdf.listener.service.impl;

import de.leipzig.htwk.gitrdf.database.common.entity.GithubRepositoryOrderMetricRatingEntity;
import de.leipzig.htwk.gitrdf.database.common.repository.GithubRepositoryOrderMetricRatingRepository;
import de.leipzig.htwk.gitrdf.listener.api.exception.NotFoundException;
import de.leipzig.htwk.gitrdf.listener.service.GithubRatingService;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;

@Service
public class GithubRatingServiceImpl implements GithubRatingService {

    private final EntityManager entityManager;
    private final GithubRepositoryOrderMetricRatingRepository ratingRepository;

    public GithubRatingServiceImpl(EntityManager entityManager,
                                   GithubRepositoryOrderMetricRatingRepository ratingRepository) {
        this.entityManager = entityManager;
        this.ratingRepository = ratingRepository;
    }

    @Override
    public List<GithubRepositoryOrderMetricRatingEntity> findByGithubRepositoryOrderId(long orderId) {
        return ratingRepository.findByGithubRepositoryOrderId(orderId);
    }

    @Override
    @Transactional
    public File getTempRdfFile(long ratingId) throws SQLException, IOException {
        GithubRepositoryOrderMetricRatingEntity entity = entityManager.find(GithubRepositoryOrderMetricRatingEntity.class, ratingId);
        if (entity == null) {
            throw NotFoundException.metricRatingEntryNotFound(ratingId);
        }

        File tempFile = Files.createTempFile("RatingRdf", ".ttl").toFile();
        try (InputStream in = entity.getRdfBlob().getBinaryStream();
             OutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile))) {
            in.transferTo(out);
        }
        return tempFile;
    }
}
