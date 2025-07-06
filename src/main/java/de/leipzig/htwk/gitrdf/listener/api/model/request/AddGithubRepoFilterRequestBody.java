package de.leipzig.htwk.gitrdf.listener.api.model.request;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.leipzig.htwk.gitrdf.listener.api.model.request.composite.filter.RepoFilterRequestModel;
import jakarta.validation.constraints.NotNull;

public class AddGithubRepoFilterRequestBody extends AddGithupRepoRequestBody {

    @NotNull
    private final RepoFilterRequestModel repositoryFilter;

    @ConstructorProperties({"owner", "repository", "repositoryFilter"})
    public AddGithubRepoFilterRequestBody(String owner, String repository, RepoFilterRequestModel repositoryFilter) {
        super(owner, repository);
        this.repositoryFilter = repositoryFilter;
    }

    public RepoFilterRequestModel getRepositoryFilter() {
        return this.repositoryFilter == null ? RepoFilterRequestModel.DISABLED : this.repositoryFilter;
    }

    @JsonIgnore // this shouldn't be a field in the springdoc openapi specification
    public boolean isRepositoryFilterEmpty() {
        return this.repositoryFilter == null;
    }

}
