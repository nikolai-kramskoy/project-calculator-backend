package org.example.projectcalculator.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.projectcalculator.dto.TeamMemberDto;
import org.example.projectcalculator.dto.request.CreateUpdateTeamMemberDtoRequest;
import org.example.projectcalculator.error.ProjectCalculatorError;
import org.example.projectcalculator.error.ProjectCalculatorException;
import org.example.projectcalculator.mapper.TeamMemberMapper;
import org.example.projectcalculator.model.Position;
import org.example.projectcalculator.model.Project;
import org.example.projectcalculator.model.TeamMember;
import org.example.projectcalculator.repository.ProjectRepository;
import org.example.projectcalculator.repository.TeamMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A {@link Service} that provides operations on {@link TeamMember}.
 */
@Slf4j
@Service
@AllArgsConstructor
public class TeamMemberService {

  private final UserService userService;
  private final ProjectService projectService;

  private final TeamMemberRepository teamMemberRepository;

  private final Clock clock;

  private final TeamMemberMapper teamMemberMapper;

  /**
   * Creates {@link TeamMember} and saves it in {@link TeamMemberRepository}.
   *
   * @param request   must be not {@code null}; {@code position} must be not blank (null or size ==
   *                  0); {@code numberOfTeamMembers} must be {@code > 0}
   * @param projectId must be {@code > 0}
   * @return {@link TeamMemberDto}
   * @throws ProjectCalculatorException if {@link Project} with specified {@code projectId} is not
   *                                    found in {@link ProjectRepository}; if {@link TeamMember}
   *                                    with specified {@link Position} already exists; if
   *                                    {@link Position} with such name does not exist
   */
  @Transactional
  public TeamMemberDto createTeamMember(final CreateUpdateTeamMemberDtoRequest request,
      final long projectId) {
    checkIfStringIsValidPosition(request.position());

    final var project = projectService.getProject(projectId);
    final var user = userService.getCurrentlyAuthenticatedUser();

    projectService.checkIfUserOwnsProject(user, project);

    final var teamMembers = teamMemberRepository.findAllByProjectId(projectId);

    checkIfTeamMemberAlreadyExists(teamMembers, request.position());

    final var teamMember = teamMemberRepository.save(
        teamMemberMapper.toTeamMember(request, project));

    project.setLastUpdatedAt(LocalDateTime.now(clock));

    log.info("Created {}", teamMember);

    return teamMemberMapper.toTeamMemberDto(teamMember);
  }

  /**
   * Returns all {@link TeamMember} of a {@link Project} from {@link TeamMemberRepository}.
   *
   * @param projectId must be {@code > 0}
   * @return {@link List} of {@link TeamMemberDto}s
   * @throws ProjectCalculatorException if {@link Project} with specified {@code projectId} is not
   *                                    found in {@link ProjectRepository}
   */
  @Transactional(readOnly = true)
  public List<TeamMemberDto> getAllTeamMembers(final long projectId) {
    final var project = projectService.getProject(projectId);
    final var user = userService.getCurrentlyAuthenticatedUser();

    projectService.checkIfUserOwnsProject(user, project);

    final var teamMembers = teamMemberRepository.findAllByProjectId(projectId);

    log.info("Get List<TeamMember> by projectId = {}: {}", projectId, teamMembers);

    return teamMembers.stream()
        .map(teamMemberMapper::toTeamMemberDto)
        .toList();
  }

  /**
   * Updates {@link TeamMember} in {@link TeamMemberRepository}.
   *
   * @param request      same requirements as in
   *                     {@link #createTeamMember(CreateUpdateTeamMemberDtoRequest, long)}
   * @param projectId    must be {@code > 0}
   * @param teamMemberId must be {@code > 0}
   * @return {@link TeamMemberDto}
   * @throws ProjectCalculatorException if {@link Project} with specified {@code projectId} is not
   *                                    found in {@link ProjectRepository}
   */
  @Transactional
  public TeamMemberDto updateTeamMember(final CreateUpdateTeamMemberDtoRequest request,
      final long projectId, final long teamMemberId) {
    checkIfStringIsValidPosition(request.position());

    final var project = projectService.getProject(projectId);
    final var user = userService.getCurrentlyAuthenticatedUser();

    projectService.checkIfUserOwnsProject(user, project);

    final var teamMembers = teamMemberRepository.findAllByProjectId(projectId);

    checkIfTeamMemberAlreadyExists(teamMembers, request.position());

    final var teamMember = teamMembers.stream()
        .filter(teamMemberLambda -> teamMemberLambda.getId() == teamMemberId)
        .findAny()
        .orElseThrow(() -> new ProjectCalculatorException(
            ProjectCalculatorError.TEAM_MEMBER_IS_NOT_FOUND_BY_ID, "teamMemberId"));

    log.info("Before update {}", teamMember);

    teamMember.setPosition(Position.valueOf(request.position()));
    teamMember.setNumberOfTeamMembers(request.numberOfTeamMembers());

    project.setLastUpdatedAt(LocalDateTime.now(clock));

    log.info("Updated {}", teamMember);

    return teamMemberMapper.toTeamMemberDto(teamMember);
  }

  /**
   * Deletes {@link TeamMember} from {@link TeamMemberRepository}.
   *
   * @param projectId    must be {@code > 0}
   * @param teamMemberId must be {@code > 0}
   * @throws ProjectCalculatorException if {@link Project} with specified {@code projectId} is not
   *                                    found in {@link ProjectRepository}; if {@link TeamMember}
   *                                    with specified {@code teamMemberId} is not found in
   *                                    {@link TeamMemberRepository}
   */
  @Transactional
  public void deleteTeamMember(final long projectId, final long teamMemberId) {
    final var teamMember = getTeamMember(projectId, teamMemberId);

    log.info("Trying to delete {}", teamMember);

    teamMember.getProject().setLastUpdatedAt(LocalDateTime.now(clock));

    teamMemberRepository.delete(teamMember);

    log.info("Deleted {}", teamMember);
  }

  /**
   * Fetches {@link TeamMember} from {@link TeamMemberRepository} by {@code projectId} and
   * {@code teamMemberId}.
   *
   * @param projectId    must be {@code > 0}
   * @param teamMemberId must be {@code > 0}
   * @return {@link TeamMember}
   * @throws ProjectCalculatorException if {@link TeamMember} with specified {@code projectId} and
   *                                    {@code teamMemberId} is not found in
   *                                    {@link TeamMemberRepository}
   */
  public TeamMember getTeamMember(final long projectId, final long teamMemberId) {
    return teamMemberRepository
        .findByIdAndProjectId(teamMemberId, projectId)
        .orElseThrow(
            () ->
                new ProjectCalculatorException(
                    ProjectCalculatorError.TEAM_MEMBER_IS_NOT_FOUND_BY_ID, "teamMemberId"));
  }

  private void checkIfStringIsValidPosition(final String stringPosition) {
    if (Arrays.stream(Position.values())
        .anyMatch(position -> position.toString().equals(stringPosition))) {
      throw new ProjectCalculatorException(ProjectCalculatorError.WRONG_POSITION, "position");
    }
  }

  private void checkIfTeamMemberAlreadyExists(final List<TeamMember> teamMembers,
      final String stringPosition) {
    if (teamMembers.stream()
        .anyMatch(teamMember -> teamMember.getPosition().toString().equals(stringPosition))) {
      throw new ProjectCalculatorException(ProjectCalculatorError.TEAM_MEMBER_ALREADY_EXISTS,
          "position");
    }
  }
}
