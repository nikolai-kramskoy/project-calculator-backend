package org.example.projectcalculator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.example.projectcalculator.dto.FeatureDto;
import org.example.projectcalculator.dto.request.CreateFeatureDtoRequest;
import org.example.projectcalculator.dto.request.UpdateFeatureDtoRequest;
import org.example.projectcalculator.service.FeatureService;

@RestController
@RequestMapping("/projects/{projectId}/features")
@Tag(name = "feature", description = "the feature API")
@Validated
@AllArgsConstructor
public class FeatureController {

  private final FeatureService featureService;

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Create feature", description = "Create feature for project (and optionally for milestone)")
  @ApiResponse(responseCode = "200", description = "Successful creation")
  public ResponseEntity<FeatureDto> createFeature(
      @Valid @RequestBody final CreateFeatureDtoRequest request,
      @PathVariable("projectId") @Min(1) final long projectId) {
    return ResponseEntity.ok(featureService.saveFeature(request, projectId));
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get features", description = "Get list of all features of project (and optionally of milestone)")
  @ApiResponse(responseCode = "200", description = "Successful query")
  public ResponseEntity<List<FeatureDto>> getAllFeatures(
      @PathVariable("projectId") @Min(1) final long projectId,
      @RequestParam(value = "milestoneId", required = false) @Min(1) final Long milestoneId) {
    return ResponseEntity.ok(featureService.getAllFeatures(projectId, milestoneId));
  }

  @PutMapping(
      path = "/{featureId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Update feature", description = "Update feature (and optionally changing milestone of this feature)")
  @ApiResponse(responseCode = "200", description = "Successful update")
  public ResponseEntity<FeatureDto> updateFeature(
      @Valid @RequestBody final UpdateFeatureDtoRequest request,
      @PathVariable("projectId") @Min(1) final long projectId,
      @PathVariable("featureId") @Min(1) final long featureId) {
    return ResponseEntity.ok(featureService.updateFeature(request, projectId, featureId));
  }

  @DeleteMapping(path = "/{featureId}")
  @Operation(summary = "Delete feature", description = "Delete feature")
  @ApiResponse(responseCode = "200", description = "Successful delete")
  public ResponseEntity<Void> deleteFeature(
      @PathVariable("projectId") @Min(1) final long projectId,
      @PathVariable("featureId") @Min(1) final long featureId) {
    featureService.deleteFeature(projectId, featureId);

    // Maybe it's better to create some EmptyDto record and return it
    return ResponseEntity.ok(null);
  }
}
