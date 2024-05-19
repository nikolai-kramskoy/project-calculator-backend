package org.example.projectcalculator.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.projectcalculator.dto.MilestoneDto;
import org.example.projectcalculator.dto.request.CreateUpdateMilestoneDtoRequest;
import org.example.projectcalculator.error.ProjectCalculatorError;
import org.example.projectcalculator.error.ProjectCalculatorException;
import org.example.projectcalculator.mapper.MilestoneMapper;
import org.example.projectcalculator.model.Milestone;
import org.example.projectcalculator.model.Project;
import org.example.projectcalculator.repository.MilestoneRepository;
import org.example.projectcalculator.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A {@link Service} that provides CRUD operations on {@link Milestone}.
 */
@Service
@AllArgsConstructor
@Slf4j
public class MilestoneService {

  private final UserService userService;
  private final ProjectService projectService;
  private final PriceService priceService;

  private final MilestoneRepository milestoneRepository;

  private final Clock clock;

  private final MilestoneMapper milestoneMapper;

  /**
   * Saves {@link Milestone} in {@link MilestoneRepository}.
   *
   * @param request   must be not {@code null}; it's {@code title}, {@code description} must be not
   *                  blank (null or size == 0); if {@code startDateTime} (same applies to
   *                  {@code endDateTime}) is not {@code null}, then it must be >=
   *                  {@code LocalDateTime.now(clock)}; if both {@code startDateTime} and
   *                  {@code endDateTime} are not {@code null}, then {@code startDateTime} must be
   *                  >= {@code endDateTime}
   * @param projectId must be {@code > 0}
   * @return {@link MilestoneDto}
   * @throws ProjectCalculatorException if {@link Project} with specified {@code projectId} is not
   *                                    found in {@link ProjectRepository}
   */
  @Transactional
  public MilestoneDto saveMilestone(
      final CreateUpdateMilestoneDtoRequest request, final long projectId) {
    final var project = projectService.getProject(projectId);
    final var user = userService.getCurrentlyAuthenticatedUser();

    projectService.checkIfUserOwnsProject(user, project);

    final var now = LocalDateTime.now(clock);

    final var milestone =
        milestoneRepository.save(
            milestoneMapper.toMilestone(request, project, now, now, BigDecimal.ZERO));

    project.setLastUpdatedAt(now);

    log.info("Saved {}", milestone);

    return milestoneMapper.toMilestoneDto(milestone);
  }

  /**
   * Returns all {@link MilestoneDto}s of {@link Project} from {@link MilestoneRepository}.
   *
   * @param projectId must be {@code > 0}
   * @return {@link List} of {@link MilestoneDto}s
   * @throws ProjectCalculatorException if {@link Project} with specified {@code projectId} is not
   *                                    found in {@link ProjectRepository}
   */
  @Transactional(readOnly = true)
  public List<MilestoneDto> getAllMilestones(final long projectId) {
    final var project = projectService.getProject(projectId);
    final var user = userService.getCurrentlyAuthenticatedUser();

    projectService.checkIfUserOwnsProject(user, project);

    final var milestones = milestoneRepository.findAllByProjectId(projectId);

    log.info("Get List<Milestone> by projectId = {}: {}", projectId, milestones);

    return milestones.stream()
        .map(milestone -> milestoneMapper.toMilestoneDto(milestone,
            priceService.computePriceInRubles(project, milestone.getEstimateInDays())))
        .toList();
  }

  /**
   * Updates {@link Milestone} in {@link MilestoneRepository} with data from {@code request}.
   *
   * @param request     same requirements as in
   *                    {@link #saveMilestone(CreateUpdateMilestoneDtoRequest, long)}
   * @param projectId   must be {@code > 0}
   * @param milestoneId must be {@code > 0}
   * @return {@link MilestoneDto}
   * @throws ProjectCalculatorException if {@link Project} with specified {@code projectId} is not
   *                                    found in {@link ProjectRepository}; if {@link Milestone}
   *                                    with specified {@code projectId} and {@code milestoneId} is
   *                                    not found in {@link MilestoneRepository}
   */
  @Transactional
  public MilestoneDto updateMilestone(
      final CreateUpdateMilestoneDtoRequest request, final long projectId, final long milestoneId) {
    final var project = projectService.getProject(projectId);
    final var user = userService.getCurrentlyAuthenticatedUser();

    projectService.checkIfUserOwnsProject(user, project);

    final var milestone = getMilestone(projectId, milestoneId);
    final var now = LocalDateTime.now(clock);

    log.info("Before update {}", milestone);

    milestone.setTitle(request.title());
    milestone.setDescription(request.description());
    milestone.setStartDateTime(request.startDateTime());
    milestone.setEndDateTime(request.endDateTime());
    milestone.setLastUpdatedAt(now);

    project.setLastUpdatedAt(now);

    log.info("Updated {}", milestone);

    return milestoneMapper.toMilestoneDto(milestone);
  }

  /**
   * Deletes {@link Milestone} from {@link MilestoneRepository}.
   *
   * @param projectId   must be {@code > 0}
   * @param milestoneId must be {@code > 0}
   * @throws ProjectCalculatorException if {@link Project} with specified {@code projectId} is not
   *                                    found in {@link ProjectRepository}; if {@link Milestone}
   *                                    with specified {@code projectId} and {@code milestoneId} is
   *                                    not found in {@link MilestoneRepository}
   */
  @Transactional
  public void deleteMilestone(final long projectId, final long milestoneId) {
    final var project = projectService.getProject(projectId);
    final var user = userService.getCurrentlyAuthenticatedUser();

    projectService.checkIfUserOwnsProject(user, project);

    final var milestone = getMilestone(projectId, milestoneId);

    log.info("Trying to delete {}", milestone);

    project.setLastUpdatedAt(LocalDateTime.now(clock));

    // I probably also need to change lastUpdatedAt of all the features
    // of the milestone...

    milestoneRepository.delete(milestone);

    log.info("Deleted {}", milestone);
  }

  /**
   * Fetches {@link Milestone} from {@link MilestoneRepository} by {@code projectId} and
   * {@code milestoneId}.
   *
   * @param projectId   must be {@code > 0}
   * @param milestoneId must be {@code > 0}
   * @return {@link Milestone}
   * @throws ProjectCalculatorException if {@link Milestone} with specified {@code projectId} and
   *                                    {@code milestoneId} is not found in
   *                                    {@link MilestoneRepository}
   */
  public Milestone getMilestone(final long projectId, final long milestoneId) {
    return milestoneRepository
        .findByIdAndProjectId(milestoneId, projectId)
        .orElseThrow(
            () ->
                new ProjectCalculatorException(
                    ProjectCalculatorError.MILESTONE_IS_NOT_FOUND_BY_ID, "milestoneId"));
  }
}
