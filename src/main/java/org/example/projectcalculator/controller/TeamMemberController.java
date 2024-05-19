package org.example.projectcalculator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.example.projectcalculator.dto.TeamMemberDto;
import org.example.projectcalculator.dto.request.CreateUpdateTeamMemberDtoRequest;
import org.example.projectcalculator.service.TeamMemberService;
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
@RequestMapping("/projects/{projectId}/team-members")
@Tag(name = "Team members")
@Validated
@AllArgsConstructor
public class TeamMemberController {

  private final TeamMemberService teamMemberService;

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Create team member", description = "Create team member for project")
  @ApiResponse(responseCode = "200", description = "Successful creation")
  public ResponseEntity<TeamMemberDto> createTeamMember(
      @PathVariable("projectId") @Min(1) final long projectId,
      @Valid @RequestBody final CreateUpdateTeamMemberDtoRequest request) {
    return ResponseEntity.ok(teamMemberService.saveTeamMember(request, projectId));
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get team members", description = "Get list of all team members of project")
  @ApiResponse(responseCode = "200", description = "Successful query")
  public ResponseEntity<List<TeamMemberDto>> getAllTeamMembers(
      @PathVariable("projectId") @Min(1) final long projectId) {
    return ResponseEntity.ok(teamMemberService.getAllTeamMembers(projectId));
  }

  @PutMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE,
      value = "/{teamMemberId}")
  @Operation(summary = "Update team member", description = "Update team member")
  @ApiResponse(responseCode = "200", description = "Successful update")
  public ResponseEntity<TeamMemberDto> updateTeamMember(
      @PathVariable("projectId") @Min(1) final long projectId,
      @PathVariable("teamMemberId") @Min(1) final long teamMemberId,
      @Valid @RequestBody final CreateUpdateTeamMemberDtoRequest request) {
    return ResponseEntity.ok(teamMemberService.updateTeamMember(request, projectId, teamMemberId));
  }

  @DeleteMapping(value = "/{teamMemberId}")
  @Operation(summary = "Delete team member", description = "Delete team member")
  @ApiResponse(responseCode = "200", description = "Successful delete")
  public ResponseEntity<Void> deleteTeamMember(
      @PathVariable("projectId") @Min(1) final long projectId,
      @PathVariable("teamMemberId") final long teamMemberId) {
    teamMemberService.deleteTeamMember(projectId, teamMemberId);

    return ResponseEntity.ok(null);
  }
}
