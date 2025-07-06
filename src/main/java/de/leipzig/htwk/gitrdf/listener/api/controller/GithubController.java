package de.leipzig.htwk.gitrdf.listener.api.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.leipzig.htwk.gitrdf.database.common.entity.GithubRepositoryFilter;
import de.leipzig.htwk.gitrdf.database.common.entity.GithubRepositoryOrderEntity;
import de.leipzig.htwk.gitrdf.listener.api.documentation.GeneralInternalServerErrorApiResponse;
import de.leipzig.htwk.gitrdf.listener.api.documentation.InvalidLongIdBadRequestApiResponse;
import de.leipzig.htwk.gitrdf.listener.api.exception.BadRequestException;
import de.leipzig.htwk.gitrdf.listener.api.model.request.AddGithubRepoFilterRequestBody;
import de.leipzig.htwk.gitrdf.listener.api.model.request.AddGithupRepoRequestBody;
import de.leipzig.htwk.gitrdf.listener.api.model.response.GithubRepositoryOrderResponse;
import de.leipzig.htwk.gitrdf.listener.api.model.response.GithubRepositorySavedResponse;
import de.leipzig.htwk.gitrdf.listener.api.model.response.error.BadRequestErrorResponse;
import de.leipzig.htwk.gitrdf.listener.api.model.response.error.NotFoundErrorResponse;
import de.leipzig.htwk.gitrdf.listener.factory.GithubRepositoryFilterFactory;
import de.leipzig.htwk.gitrdf.listener.service.GithubService;
import de.leipzig.htwk.gitrdf.listener.utils.LongUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/listener-service/api/v1/github")
@Tag(name = "Github API")
public class GithubController {

    private final GithubService githubService;

    private final GithubRepositoryFilterFactory githubRepositoryFilterFactory;

    @Operation(summary = "Get all github order entries")
    @ApiResponse(
            responseCode = "200",
            description = "All github order entries",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(
                            schema = @Schema(implementation = GithubRepositoryOrderResponse.class))))
    @GeneralInternalServerErrorApiResponse
    @GetMapping
    public List<GithubRepositoryOrderResponse> getAllGithubRepositoryOrderEntries() {
        List<GithubRepositoryOrderEntity> results = githubService.findAll();
        return  GithubRepositoryOrderResponse.toList(results);
    }

    @Operation(summary = "Add a github entry to the queue")
    @ApiResponse(
            responseCode = "200",
            description = "Added the specified github entry to the queue",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GithubRepositorySavedResponse.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BadRequestErrorResponse.class),
                    examples = {
                            @ExampleObject(
                                    name = "No owner was specified",
                                    description = "No owner was specified",
                                    value = "{\"status\": \"Bad Request\", \"reason\": \"No owner was specified\", \"solution\": \"Specify an owner. For example: 'dotnet' (who is the owner for example of the repo 'core')\"}"),
                            @ExampleObject(
                                    name = "No repository was specified",
                                    description = "No repository was specified",
                                    value = "{\"status\": \"Bad Request\", \"reason\": \"No repository was specified\", \"solution\": \"Specify a repository. For example: 'core' (the owner 'dotnet' provides for example a 'core' repository)\"}")}))
    @GeneralInternalServerErrorApiResponse
    @PostMapping("/queue")
    public GithubRepositorySavedResponse addGithubRepo(@RequestBody AddGithupRepoRequestBody requestBody) {

        if (StringUtils.isBlank(requestBody.getOwner())) {
            throw BadRequestException.noOwnerSpecified();
        }

        if (StringUtils.isBlank(requestBody.getRepository())) {
            throw BadRequestException.noRepositorySpecified();
        }

        long id = githubService.insertGithubRepositoryIntoQueue(
                requestBody.getOwner(),
                requestBody.getRepository(),
                GithubRepositoryFilter.DEFAULT);

        return new GithubRepositorySavedResponse(id);
    }

    @Operation(
            summary = "Add a github entry to the queue while also specifying filter properties",
            description = "At least one filter property has to be set. Left out filter properties will be disabled.")
    @ApiResponse(
            responseCode = "200",
            description = "Added the specified github entry with the specified filter properties to the queue",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GithubRepositorySavedResponse.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BadRequestErrorResponse.class),
                    examples = {
                            @ExampleObject(
                                    name = "No owner was specified",
                                    description = "No owner was specified",
                                    value = "{\"status\": \"Bad Request\", \"reason\": \"No owner was specified\", \"solution\": \"Specify an owner. For example: 'dotnet' (who is the owner for example of the repo 'core')\"}"),
                            @ExampleObject(
                                    name = "No repository was specified",
                                    description = "No repository was specified",
                                    value = "{\"status\": \"Bad Request\", \"reason\": \"No repository was specified\", \"solution\": \"Specify a repository. For example: 'core' (the owner 'dotnet' provides for example a 'core' repository)\"}"),
                            @ExampleObject(
                                    name = "Can't disable all filter options",
                                    description = "Can't disable all filter options",
                                    value = "{\"status\": \"Bad Request\", \"reason\": \"All repository filter options are disabled\", \"solution\": \"Enable at least one repository filter option\"}")}))
    @GeneralInternalServerErrorApiResponse
    @PostMapping("/queue/filter")
    public GithubRepositorySavedResponse addGithubRepoWithFilters(
            @RequestBody AddGithubRepoFilterRequestBody requestBody) {

        if (StringUtils.isBlank(requestBody.getOwner())) {
            throw BadRequestException.noOwnerSpecified();
        }

        if (StringUtils.isBlank(requestBody.getRepository())) {
            throw BadRequestException.noRepositorySpecified();
        }

        if (requestBody.isRepositoryFilterEmpty() || requestBody.getRepositoryFilter().areAllFilterOptionsDisabled()) {
            throw BadRequestException.cantDisableAllRepositoryFilterOptions();
        }

        GithubRepositoryFilter githubRepositoryFilter
                = githubRepositoryFilterFactory.fromRepoFilterRequestModel(requestBody.getRepositoryFilter());

        long id = githubService.insertGithubRepositoryIntoQueue(
                requestBody.getOwner(), requestBody.getRepository(), githubRepositoryFilter);

        return new GithubRepositorySavedResponse(id);
    }

    @Operation(summary = "Download the produced rdf file")
    @ApiResponse(
            responseCode = "200",
            description = "Rdf file",
            content = @Content(
                    mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
    @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BadRequestErrorResponse.class),
                    examples = {
                            @ExampleObject(
                                    name = "Invalid id was specified",
                                    description = "Invalid id was specified",
                                    value = "{\"status\": \"Bad Request\", \"reason\": \"Invalid id 'blub' was given\", \"solution\": \"Provide a valid id. Example id: 55\"}"),
                            @ExampleObject(
                                    name = "No rdf file is available yet",
                                    description = "No rdf file is available yet",
                                    value = "{\"status\": \"Bad Request\", \"reason\": \"Specified repository was not yet processed and therefore also doesnt contain a rdf file to download\", \"solution\": \"Wait until the repository was successfully processed (ie. status of repository is 'DONE')\"}")}))
    @ApiResponse(
            responseCode = "404",
            description = "Not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = NotFoundErrorResponse.class),
                    examples = {
                            @ExampleObject(
                                    name = "No github to rdf entry found",
                                    description = "No github to rdf entry found",
                                    value = "{\"status\": \"Not found\", \"reason\": \"No github to rdf entry found for id '3'\", \"solution\": \"Provide an id for an existing github to rdf entry\"}")}))
    @GeneralInternalServerErrorApiResponse
    @GetMapping(path = "/rdf/download/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody Resource downloadRdf(@PathVariable("id") String id, HttpServletResponse httpServletResponse) throws SQLException, IOException {

        long longId = LongUtils.convertStringToLongIdOrThrowException(id);

        if (!githubService.isRdfFileAvailable(longId)) {
            throw BadRequestException.noRdfFileAvailableYet();
        }

        File tempRdfFile = githubService.getTempRdfFile(longId);

        Resource responseResource = new InputStreamResource(new BufferedInputStream(new FileInputStream(tempRdfFile)));

        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\"rdf.ttl\"");

        return responseResource;
    }

    @Operation(summary = "Delete specified github order entry and all connected data")
    @ApiResponse(
            responseCode = "204",
            description = "Successfully deleted github entry")
    @InvalidLongIdBadRequestApiResponse
    @ApiResponse(
            responseCode = "404",
            description = "Not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = NotFoundErrorResponse.class),
                    examples = {
                            @ExampleObject(
                                    name = "No github to rdf entry found",
                                    description = "No github to rdf entry found",
                                    value = "{\"status\": \"Not found\", \"reason\": \"No github to rdf entry found for id '3'\", \"solution\": \"Provide an id for an existing github to rdf entry\"}")}))
    @GeneralInternalServerErrorApiResponse
    @DeleteMapping(path = "/rdf/completedelete/{id}")
    public ResponseEntity<Void> deleteGitAndRdf(@PathVariable("id") String id) {

        long longId = LongUtils.convertStringToLongIdOrThrowException(id);
        githubService.completeDelete(longId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
