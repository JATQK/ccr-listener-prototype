package de.leipzig.htwk.gitrdf.listener.factory;

import org.springframework.stereotype.Component;

import de.leipzig.htwk.gitrdf.database.common.entity.GitCommitRepositoryFilter;
import de.leipzig.htwk.gitrdf.database.common.entity.GithubIssueRepositoryFilter;
import de.leipzig.htwk.gitrdf.database.common.entity.GithubRepositoryFilter;
import de.leipzig.htwk.gitrdf.listener.api.model.request.composite.filter.GitCommitFilterRequestModel;
import de.leipzig.htwk.gitrdf.listener.api.model.request.composite.filter.GithubIssueFilterRequestModel;
import de.leipzig.htwk.gitrdf.listener.api.model.request.composite.filter.RepoFilterRequestModel;

@Component
public class GithubRepositoryFilterFactory {

    public GithubRepositoryFilter fromRepoFilterRequestModel(RepoFilterRequestModel repoFilterRequestModel) {

        if (repoFilterRequestModel == null) {
            return GithubRepositoryFilter.ENABLED;
        }

        return convertFromNonNullRepoFilterRequestModel(repoFilterRequestModel);
    }

    private GithubRepositoryFilter convertFromNonNullRepoFilterRequestModel(
            RepoFilterRequestModel repoFilterRequestModel) {

        if (repoFilterRequestModel == null)  {
            throw new RuntimeException("Given repoFilterRequestModel is null but shouldn't be");
        }

        return new GithubRepositoryFilter(
                convertFromGitCommitFilterRequestModel(repoFilterRequestModel.getGitCommitFilter()),
                convertFromGithubIssueFilterRequestModel(repoFilterRequestModel.getGithubIssueFilter()));
    }

    private GitCommitRepositoryFilter convertFromGitCommitFilterRequestModel(GitCommitFilterRequestModel model) {
        return new GitCommitRepositoryFilter(
                model.isCommitHashEnabled(),
                model.isAuthorNameEnabled(),
                model.isAuthorEmailEnabled(),
                model.isAuthorDateEnabled(),
                model.isCommitDateEnabled(),
                model.isCommitterNameEnabled(),
                model.isCommitterEmailEnabled(),
                model.isCommitMessageEnabled(),
                model.isCommitDiffEnabled(),
                model.isCommitBranchEnabled(),
                model.isCommitTagEnabled(),
                model.isBranchSnapshotEnabled());
    }

    private GithubIssueRepositoryFilter convertFromGithubIssueFilterRequestModel(GithubIssueFilterRequestModel model) {
        return new GithubIssueRepositoryFilter(
                model.isIssueIdEnabled(),
                model.isIssueNumberEnabled(),
                model.isIssueStateEnabled(),
                model.isIssueTitleEnabled(),
                model.isIssueBodyEnabled(),
                model.isIssueUserEnabled(),
                model.isIssueLabelsEnabled(),
                model.isIssueAssigneesEnabled(),
                model.isIssueMilestoneEnabled(),
                model.isIssueCreatedAtEnabled(),
                model.isIssueUpdatedAtEnabled(),
                model.isIssueClosedAtEnabled(),
                model.isEnableIssueReviewers(),
                model.isEnableIssueMergedBy(),
                model.isEnableIssueMergedInfo(),
                model.isEnableIssueComments());
    }

}
