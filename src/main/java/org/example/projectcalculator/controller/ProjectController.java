package org.example.projectcalculator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.example.projectcalculator.dto.ProjectDto;
import org.example.projectcalculator.dto.request.CreateUpdateProjectDtoRequest;
import org.example.projectcalculator.service.ProjectService;
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

@RestController
@RequestMapping("/projects")
@Tag(name = "project", description = "the project API")
@Validated
@AllArgsConstructor
public class ProjectController {

  private final ProjectService projectService;

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Create project", description = "Create project")
  @ApiResponse(responseCode = "200", description = "Successful creation")
  public ResponseEntity<ProjectDto> createProject(
      @Valid @RequestBody final CreateUpdateProjectDtoRequest request) {
    return ResponseEntity.ok(projectService.saveProject(request));
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get projects", description = "Get list of all projects")
  @ApiResponse(responseCode = "200", description = "Successful query")
  public ResponseEntity<List<ProjectDto>> getAllProjects() {
    return ResponseEntity.ok(projectService.getAllProjects());
  }

  @PutMapping(
      path = "/{projectId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Update project", description = "Update project")
  @ApiResponse(responseCode = "200", description = "Successful update")
  public ResponseEntity<ProjectDto> updateProject(
      @Valid @RequestBody final CreateUpdateProjectDtoRequest request,
      @PathVariable("projectId") @Min(1) final long projectId) {
    return ResponseEntity.ok(projectService.updateProject(request, projectId));
  }

  @DeleteMapping(path = "/{projectId}")
  @Operation(summary = "Delete project", description = "Delete project")
  @ApiResponse(responseCode = "200", description = "Successful delete")
  public ResponseEntity<Void> deleteProject(
      @PathVariable("projectId") @Min(1) final long projectId) {
    projectService.deleteProject(projectId);

    // Maybe it's better to create some EmptyDto record and return it
    return ResponseEntity.ok(null);
  }
}
