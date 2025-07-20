package de.leipzig.htwk.gitrdf.listener.api.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
import de.leipzig.htwk.gitrdf.listener.service.RatingsService;
import de.leipzig.htwk.gitrdf.listener.utils.LongUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/listener-service/api/v1/github")
@Tag(name = "Github API")
public class GithubController {

        private final GithubService githubService;
        private final RatingsService ratingsService; // Add this dependency
        private final GithubRepositoryFilterFactory githubRepositoryFilterFactory;

        @Operation(summary = "Get all github order entries")
        @ApiResponse(responseCode = "200", description = "All github order entries", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = GithubRepositoryOrderResponse.class))))
        @GeneralInternalServerErrorApiResponse
        @GetMapping
        public List<GithubRepositoryOrderResponse> getAllGithubRepositoryOrderEntries() {
                List<GithubRepositoryOrderEntity> results = githubService.findAll();

                // Convert to response with ratings and statistics counts
                return results.stream()
                                .map(entity -> {
                                        try {
                                                // Get the statistics for this order
                                                RatingsService.OrderStatistics stats = ratingsService
                                                                .getOrderStatistics(entity.getId());

                                                // Use the fromWithRatings method to include the counts
                                                return GithubRepositoryOrderResponse.fromWithRatings(
                                                                entity,
                                                                stats.getTotalRatings(),
                                                                stats.getUniqueMetrics());
                                        } catch (Exception e) {
                                                // If there's an error getting statistics, log it and return basic
                                                // response
                                                log.warn("Could not get statistics for order ID {}: {}", entity.getId(),
                                                                e.getMessage());
                                                return GithubRepositoryOrderResponse.from(entity);
                                        }
                                })
                                .collect(Collectors.toList());
        }

        @Operation(summary = "Add a github entry to the queue")
        @ApiResponse(responseCode = "200", description = "Added the specified github entry to the queue", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GithubRepositorySavedResponse.class)))
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestErrorResponse.class), examples = {
                        @ExampleObject(name = "No owner was specified", description = "No owner was specified", value = "{\"status\": \"Bad Request\", \"reason\": \"No owner was specified\", \"solution\": \"Specify an owner. For example: 'dotnet' (who is the owner for example of the repo 'core')\"}"),
                        @ExampleObject(name = "No repository was specified", description = "No repository was specified", value = "{\"status\": \"Bad Request\", \"reason\": \"No repository was specified\", \"solution\": \"Specify a repository. For example: 'core' (the owner 'dotnet' provides for example a 'core' repository)\"}") }))
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

        @Operation(summary = "Add a github entry to the queue while also specifying filter properties", description = "At least one filter property has to be set. Left out filter properties will be disabled.")
        @ApiResponse(responseCode = "200", description = "Added the specified github entry with the specified filter properties to the queue", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GithubRepositorySavedResponse.class)))
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestErrorResponse.class), examples = {
                        @ExampleObject(name = "No owner was specified", description = "No owner was specified", value = "{\"status\": \"Bad Request\", \"reason\": \"No owner was specified\", \"solution\": \"Specify an owner. For example: 'dotnet' (who is the owner for example of the repo 'core')\"}"),
                        @ExampleObject(name = "No repository was specified", description = "No repository was specified", value = "{\"status\": \"Bad Request\", \"reason\": \"No repository was specified\", \"solution\": \"Specify a repository. For example: 'core' (the owner 'dotnet' provides for example a 'core' repository)\"}"),
                        @ExampleObject(name = "Can't disable all filter options", description = "Can't disable all filter options", value = "{\"status\": \"Bad Request\", \"reason\": \"All repository filter options are disabled\", \"solution\": \"Enable at least one repository filter option\"}")
        }))
        @GeneralInternalServerErrorApiResponse
        @PostMapping("/queue/filter")
        public GithubRepositorySavedResponse addGithubRepoWithFilter(
                        @RequestBody AddGithubRepoFilterRequestBody requestBody) {

                if (StringUtils.isBlank(requestBody.getOwner())) {
                        throw BadRequestException.noOwnerSpecified();
                }

                if (StringUtils.isBlank(requestBody.getRepository())) {
                        throw BadRequestException.noRepositorySpecified();
                }

                if (requestBody.isRepositoryFilterEmpty()
                                || requestBody.getRepositoryFilter().areAllFilterOptionsDisabled()) {
                        throw BadRequestException.cantDisableAllRepositoryFilterOptions();
                }

                // Use the correct method name from the factory
                GithubRepositoryFilter githubRepositoryFilter = githubRepositoryFilterFactory
                                .fromRepoFilterRequestModel(
                                                requestBody.getRepositoryFilter());

                long id = githubService.insertGithubRepositoryIntoQueue(
                                requestBody.getOwner(),
                                requestBody.getRepository(),
                                githubRepositoryFilter);

                return new GithubRepositorySavedResponse(id);
        }

        @Operation(summary = "Check if RDF file is available for download")
        @ApiResponse(responseCode = "200", description = "RDF file availability status")
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestErrorResponse.class)))
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = NotFoundErrorResponse.class)))
        @GeneralInternalServerErrorApiResponse
        @InvalidLongIdBadRequestApiResponse
        @GetMapping("/rdf/{id}")
        public ResponseEntity<String> checkRdfAvailability(@PathVariable String id) {

                // Use the correct method to validate and convert string to long
                long parsedId = LongUtils.convertStringToLongIdOrThrowException(id);
                boolean isAvailable = githubService.isRdfFileAvailable(parsedId);

                if (isAvailable) {
                        return ResponseEntity.ok("RDF file is available");
                } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("RDF file not available");
                }
        }

        @Operation(summary = "Download RDF file")
        @ApiResponse(responseCode = "200", description = "RDF file download", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestErrorResponse.class)))
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = NotFoundErrorResponse.class)))
        @GeneralInternalServerErrorApiResponse
        @InvalidLongIdBadRequestApiResponse
        @GetMapping(value = "/rdf/download/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
        @ResponseBody
        public ResponseEntity<Resource> downloadRdfFile(@PathVariable String id) throws SQLException, IOException {

                // Use the correct method to validate and convert string to long
                long parsedId = LongUtils.convertStringToLongIdOrThrowException(id);

                File rdfFile = githubService.getTempRdfFile(parsedId);
                InputStreamResource resource = new InputStreamResource(
                                new BufferedInputStream(new FileInputStream(rdfFile)));

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + rdfFile.getName())
                                .contentLength(rdfFile.length())
                                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                .body(resource);
        }

        @Operation(summary = "Complete delete of github repository and all connected resources")
        @ApiResponse(responseCode = "200", description = "Successfully deleted")
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestErrorResponse.class)))
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = NotFoundErrorResponse.class)))
        @GeneralInternalServerErrorApiResponse
        @InvalidLongIdBadRequestApiResponse
        @DeleteMapping("/rdf/completedelete/{id}")
        public ResponseEntity<String> completeDelete(@PathVariable String id) {

                // Use the correct method to validate and convert string to long
                long parsedId = LongUtils.convertStringToLongIdOrThrowException(id);
                githubService.completeDelete(parsedId);

                return ResponseEntity.ok("Repository and all connected resources have been successfully deleted");
        }
}