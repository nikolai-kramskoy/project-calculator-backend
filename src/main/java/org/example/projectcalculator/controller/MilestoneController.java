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
import org.springframework.web.bind.annotation.RestController;
import org.example.projectcalculator.dto.MilestoneDto;
import org.example.projectcalculator.dto.request.CreateUpdateMilestoneDtoRequest;
import org.example.projectcalculator.service.MilestoneService;

@RestController
@RequestMapping("/projects/{projectId}/milestones")
@Tag(name = "milestone", description = "the milestone API")
@Validated
@AllArgsConstructor
public class MilestoneController {

  private final MilestoneService milestoneService;

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Create milestone", description = "Create milestone for project")
  @ApiResponse(responseCode = "200", description = "Successful creation")
  public ResponseEntity<MilestoneDto> createMilestone(
      @Valid @RequestBody final CreateUpdateMilestoneDtoRequest request,
      @PathVariable("projectId") @Min(1) long projectId) {
    return ResponseEntity.ok(milestoneService.saveMilestone(request, projectId));
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get milestones", description = "Get list of all milestones of project")
  @ApiResponse(responseCode = "200", description = "Successful query")
  public ResponseEntity<List<MilestoneDto>> getAllMilestones(
      @PathVariable("projectId") @Min(1) final long projectId) {
    return ResponseEntity.ok(milestoneService.getAllMilestones(projectId));
  }

  @PutMapping(
      path = "/{milestoneId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Update milestone", description = "Update milestone")
  @ApiResponse(responseCode = "200", description = "Successful update")
  public ResponseEntity<MilestoneDto> updateMilestone(
      @Valid @RequestBody final CreateUpdateMilestoneDtoRequest request,
      @PathVariable("projectId") @Min(1) final long projectId,
      @PathVariable("milestoneId") @Min(1) final long milestoneId) {
    return ResponseEntity.ok(milestoneService.updateMilestone(request, projectId, milestoneId));
  }

  @DeleteMapping(path = "/{milestoneId}")
  @Operation(summary = "Delete milestone", description = "Delete milestone")
  @ApiResponse(responseCode = "200", description = "Successful delete")
  public ResponseEntity<Void> deleteMilestone(
      @PathVariable("projectId") @Min(1) final long projectId,
      @PathVariable("milestoneId") @Min(1) final long milestoneId) {
    milestoneService.deleteMilestone(projectId, milestoneId);

    // Maybe it's better to create some EmptyDto record and return it
    return ResponseEntity.ok(null);
  }
}
