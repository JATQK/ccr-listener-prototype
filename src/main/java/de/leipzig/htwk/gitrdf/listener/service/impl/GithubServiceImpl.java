package de.leipzig.htwk.gitrdf.listener.service.impl;

import de.leipzig.htwk.gitrdf.database.common.entity.GithubRepositoryFilter;
import de.leipzig.htwk.gitrdf.database.common.entity.GithubRepositoryOrderEntity;
import de.leipzig.htwk.gitrdf.database.common.entity.enums.GitRepositoryOrderStatus;
import de.leipzig.htwk.gitrdf.database.common.entity.lob.GithubRepositoryOrderEntityLobs;
import de.leipzig.htwk.gitrdf.database.common.repository.GithubRepositoryOrderRepository;
import de.leipzig.htwk.gitrdf.listener.api.exception.NotFoundException;
import de.leipzig.htwk.gitrdf.listener.service.GithubService;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;

@Service
public class GithubServiceImpl implements GithubService {

    private final EntityManager entityManager;

    private final GithubRepositoryOrderRepository githubRepositoryOrderRepository;

    public GithubServiceImpl(
            EntityManager entityManager,
            GithubRepositoryOrderRepository githubRepositoryOrderRepository) {

        this.entityManager = entityManager;
        this.githubRepositoryOrderRepository = githubRepositoryOrderRepository;
    }

    @Override
    public List<GithubRepositoryOrderEntity> findAll() {
        return githubRepositoryOrderRepository.findAll();
    }

    @Transactional
    @Override
    public long insertGithubRepositoryIntoQueue(
            String owner,
            String repository,
            GithubRepositoryFilter githubRepositoryFilter) {

        GithubRepositoryOrderEntity githubRepositoryOrderEntity
                = GithubRepositoryOrderEntity.newOrder(owner, repository, githubRepositoryFilter);

        entityManager.persist(githubRepositoryOrderEntity);

        GithubRepositoryOrderEntityLobs githubRepositoryOrderEntityLobs = new GithubRepositoryOrderEntityLobs();
        githubRepositoryOrderEntityLobs.setOrderEntity(githubRepositoryOrderEntity);
        githubRepositoryOrderEntityLobs.setRdfFile(null);

        entityManager.persist(githubRepositoryOrderEntityLobs);

        return githubRepositoryOrderEntity.getId();
    }

    @Transactional
    @Override
    public boolean isRdfFileAvailable(long id) {

        GithubRepositoryOrderEntity githubRepositoryOrderEntity
                = entityManager.find(GithubRepositoryOrderEntity.class, id);

        if (githubRepositoryOrderEntity == null) {
            throw NotFoundException.githubEntryNotFound(id);
        }

        return githubRepositoryOrderEntity.getStatus().equals(GitRepositoryOrderStatus.DONE);
    }

    @Transactional
    @Override
    public File getTempRdfFile(long id) throws SQLException, IOException {

        GithubRepositoryOrderEntityLobs lob = entityManager.find(GithubRepositoryOrderEntityLobs.class, id);

        File tempFile = Files.createTempFile("RdfFileData", ".ttl").toFile();

        try (InputStream binaryInputStream = lob.getRdfFile().getBinaryStream()) {
            try (OutputStream fileOutputStream = new BufferedOutputStream(new FileOutputStream(tempFile))) {
                binaryInputStream.transferTo(fileOutputStream);
            }
        }

        return tempFile;
    }

    @Transactional
    @Override
    public void completeDelete(long id) {

        GithubRepositoryOrderEntityLobs githubRepositoryOrderEntityLobs
                = entityManager.find(GithubRepositoryOrderEntityLobs.class, id);

        GithubRepositoryOrderEntity githubRepositoryOrderEntity
                = entityManager.find(GithubRepositoryOrderEntity.class, id);

        if (githubRepositoryOrderEntity == null) {
            throw NotFoundException.githubEntryNotFound(id);
        }

        entityManager.remove(githubRepositoryOrderEntityLobs);
        entityManager.remove(githubRepositoryOrderEntity);
    }

}
