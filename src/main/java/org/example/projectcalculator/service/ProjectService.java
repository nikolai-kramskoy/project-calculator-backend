package org.example.projectcalculator.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.projectcalculator.dto.ProjectDto;
import org.example.projectcalculator.dto.request.CreateUpdateProjectDtoRequest;
import org.example.projectcalculator.error.ProjectCalculatorError;
import org.example.projectcalculator.error.ProjectCalculatorException;
import org.example.projectcalculator.mapper.ProjectMapper;
import org.example.projectcalculator.model.Position;
import org.example.projectcalculator.model.Project;
import org.example.projectcalculator.model.Rate;
import org.example.projectcalculator.model.TeamMember;
import org.example.projectcalculator.model.User;
import org.example.projectcalculator.repository.ProjectRepository;
import org.example.projectcalculator.repository.RateRepository;
import org.example.projectcalculator.repository.TeamMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A {@link Service} that provides operations on {@link Project}.
 */
@Service
@AllArgsConstructor
@Slf4j
public class ProjectService {

  private final UserService userService;
  private final PriceService priceService;

  private final ProjectRepository projectRepository;
  private final RateRepository rateRepository;
  private final TeamMemberRepository teamMemberRepository;

  private final Clock clock;

  private final ProjectMapper projectMapper;

  /**
   * Saves {@link Project} in {@link ProjectRepository}.
   *
   * @param request must be not {@code null}; it's {@code title}, {@code description} and
   *                {@code client} must be not blank (null or size == 0)
   * @return {@link ProjectDto}
   */
  @Transactional
  public ProjectDto saveProject(final CreateUpdateProjectDtoRequest request) {
    final User creator = userService.getCurrentlyAuthenticatedUser();
    final var now = LocalDateTime.now(clock);

    final Project project = projectRepository.save(
        projectMapper.toProject(request, creator, BigDecimal.ZERO, now, now));

    log.info("Create {}", project);

    // Create default rates for project

    final List<Rate> rates = Arrays.stream(Position.values())
        .map(position -> new Rate(0L, position, position.getDefaultRateInRublesPerHour(), project))
        .toList();

    rateRepository.saveAll(rates);

    log.info("Create default List<Rate>: {}", rates);

    // Create default team members for project

    final List<TeamMember> teamMembers = List.of(
        new TeamMember(0L, Position.REGULAR_DEVELOPER, new BigDecimal("1"), project),
        new TeamMember(0L, Position.QA_ENGINEER, new BigDecimal("0.25"), project),
        new TeamMember(0L, Position.PROJECT_MANAGER, new BigDecimal("0.25"), project));

    teamMemberRepository.saveAll(teamMembers);

    log.info("Create default List<TeamMember>: {}", teamMembers);

    return projectMapper.toProjectDto(project);
  }

  /**
   * Returns all {@link ProjectDto}s  from {@link ProjectRepository}.
   *
   * @return {@link List} of {@link ProjectDto}s
   */
  @Transactional(readOnly = true)
  public List<ProjectDto> getAllProjects() {
    final User user = userService.getCurrentlyAuthenticatedUser();
    final List<Project> projects = getAllProjectsWithRatesAndTeamMembers(user.getId());

    log.info("Get List<Project> by creatorId = {}: {}", user.getId(), projects);

    return projects.stream()
        .map(project -> projectMapper.toProjectDto(project,
            priceService.computePriceInRubles(project, project.getEstimateInDays())))
        .toList();
  }

  /**
   * Updates {@link Project} in {@link ProjectRepository} with data from {@code request}.
   *
   * @param request   same requirements as in {@link #saveProject(CreateUpdateProjectDtoRequest)}
   * @param projectId must be {@code > 0}
   * @return {@link ProjectDto}
   * @throws ProjectCalculatorException if {@link Project} with specified {@code projectId} is not
   *                                    found in {@link ProjectRepository}
   */
  @Transactional
  public ProjectDto updateProject(final CreateUpdateProjectDtoRequest request,
      final long projectId) {
    final Project project = getProject(projectId);
    final User user = userService.getCurrentlyAuthenticatedUser();

    checkIfUserOwnsProject(user, project);

    log.info("Before update {}", project);

    project.setTitle(request.title());
    project.setDescription(request.description());
    project.setClient(request.client());
    project.setLastUpdatedAt(LocalDateTime.now(clock));

    log.info("Updated {}", project);

    return projectMapper.toProjectDto(project);
  }

  /**
   * Deletes {@link Project} from {@link ProjectRepository}.
   *
   * @param projectId must be {@code > 0}
   * @throws ProjectCalculatorException if {@link Project} with specified {@code projectId} is not
   *                                    found in {@link ProjectRepository}
   */
  @Transactional
  public void deleteProject(final long projectId) {
    final Project project = getProject(projectId);
    final User user = userService.getCurrentlyAuthenticatedUser();

    checkIfUserOwnsProject(user, project);

    log.info("Trying to delete {}", project);

    projectRepository.delete(project);

    log.info("Deleted {}", project);
  }

  /**
   * Fetches {@link Project} from {@link ProjectRepository} by {@code projectId}.
   *
   * @param projectId must be {@code > 0}
   * @return {@link Project}
   * @throws ProjectCalculatorException if {@link Project} with specified {@code projectId} is not
   *                                    found in {@link ProjectRepository}
   */
  public Project getProject(final long projectId) {
    return projectRepository
        .findById(projectId)
        .orElseThrow(
            () ->
                new ProjectCalculatorException(
                    ProjectCalculatorError.PROJECT_IS_NOT_FOUND_BY_ID, "projectId"));
  }

  public void checkIfUserOwnsProject(final User user, final Project project) {
    if (project.getCreator().getId() != user.getId()) {
      throw new ProjectCalculatorException(ProjectCalculatorError.PROJECT_IS_NOT_FOUND_BY_ID,
          "projectId");
    }
  }

  private List<Project> getAllProjectsWithRatesAndTeamMembers(final long creatorId) {
    // an interesting way to avoid N+1 problem

    // https://vladmihalcea.com/hibernate-multiplebagfetchexception/
    // https://vladmihalcea.com/spring-data-jpa-multiplebagfetchexception/

    final var projectsWithRates = projectRepository.findAllWithRates(creatorId);
    projectRepository.findAllWithTeamMembers(creatorId);

    return projectsWithRates;
  }
}
