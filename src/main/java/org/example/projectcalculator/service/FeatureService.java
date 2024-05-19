package org.example.projectcalculator.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.projectcalculator.dto.FeatureDto;
import org.example.projectcalculator.dto.request.CreateFeatureDtoRequest;
import org.example.projectcalculator.dto.request.UpdateFeatureDtoRequest;
import org.example.projectcalculator.error.ProjectCalculatorError;
import org.example.projectcalculator.error.ProjectCalculatorException;
import org.example.projectcalculator.mapper.FeatureMapper;
import org.example.projectcalculator.model.Feature;
import org.example.projectcalculator.model.Milestone;
import org.example.projectcalculator.model.Project;
import org.example.projectcalculator.model.User;
import org.example.projectcalculator.repository.FeatureRepository;
import org.example.projectcalculator.repository.MilestoneRepository;
import org.example.projectcalculator.repository.ProjectRepository;
import org.example.projectcalculator.repository.RateRepository;
import org.example.projectcalculator.repository.TeamMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A {@link Service} that provides operations on {@link Feature}.
 */
@Service
@AllArgsConstructor
@Slf4j
public class FeatureService {

  private final UserService userService;
  private final ProjectService projectService;
  private final MilestoneService milestoneService;
  private final PriceService priceService;

  private final FeatureRepository featureRepository;
  private final RateRepository rateRepository;
  private final TeamMemberRepository teamMemberRepository;

  private final Clock clock;

  private final FeatureMapper featureMapper;

  /**
   * Saves {@link Feature} in {@link FeatureRepository}.
   *
   * @param request   must be not {@code null}; it's {@code title}, {@code description} must be not
   *                  blank (null or size == 0); if milestoneId it is not {@code null}, then it must
   *                  be {@code > 0};
   *                  {@code bestCaseEstimateInDays <= mostLikelyEstimateInDays <=
   *                  worstCaseEstimateInDays} must be {@code true} and all estimates must be
   *                  {@code > 0.0}
   * @param projectId must be {@code > 0}
   * @return {@link FeatureDto}
   * @throws ProjectCalculatorException if {@link Project} with specified {@code projectId} is not
   *                                    found in {@link ProjectRepository}; if {@link Milestone}
   *                                    with specified {@code projectId} and {@code milestoneId} is
   *                                    not found in {@link MilestoneRepository}
   */
  @Transactional
  public FeatureDto saveFeature(final CreateFeatureDtoRequest request, final long projectId) {
    final Project project = projectService.getProject(projectId);
    final User user = userService.getCurrentlyAuthenticatedUser();

    projectService.checkIfUserOwnsProject(user, project);

    final Long milestoneId = request.getMilestoneId();
    final Milestone milestone =
        (milestoneId != null) ? milestoneService.getMilestone(projectId, milestoneId) : null;
    final var now = LocalDateTime.now(clock);

    final Feature feature =
        featureRepository.save(
            featureMapper.toFeature(request, project, milestone, now, now));

    // need to add feature's estimate to project and milestone

    project.setEstimateInDays(project.getEstimateInDays().add(feature.getEstimateInDays()));
    project.setLastUpdatedAt(now);

    if (milestone != null) {
      milestone.setEstimateInDays(milestone.getEstimateInDays().add(feature.getEstimateInDays()));
      milestone.setLastUpdatedAt(now);
    }

    log.info("Saved {}", feature);

    return featureMapper.toFeatureDto(feature);
  }

  /**
   * Returns all {@link FeatureDto}s of {@link Project} from {@link FeatureRepository} with optional
   * {@code milestoneId} filtration (this means that if milestone is not {@code null}, then this
   * method will return all {@link FeatureDto}s of specified {@link Milestone}).
   *
   * @param projectId   must be {@code > 0}
   * @param milestoneId if it is not {@code null}, then it must be {@code > 0}
   * @return {@link List} of {@link FeatureDto}s
   * @throws ProjectCalculatorException if {@link Project} with specified {@code projectId} is not
   *                                    found in {@link ProjectRepository}; if {@link Milestone}
   *                                    with specified {@code milestoneId} is not found in
   *                                    {@link MilestoneRepository}
   */
  @Transactional(readOnly = true)
  public List<FeatureDto> getAllFeatures(final long projectId, final Long milestoneId) {
    final Project project = projectService.getProject(projectId);
    final User user = userService.getCurrentlyAuthenticatedUser();

    projectService.checkIfUserOwnsProject(user, project);

    final List<Feature> features;

    if (milestoneId != null) {
      features = featureRepository.findAllByProjectIdAndMilestoneId(projectId, milestoneId);

      log.info(
          "Get List<Feature> by projectId = {}, milestoneId = {}: {}",
          projectId, milestoneId, features);
    } else {
      features = featureRepository.findAllByProjectId(projectId);

      log.info("Get List<Feature> by projectId = {}: {}", projectId, features);
    }

    return features.stream()
        .map(feature -> featureMapper.toFeatureDto(feature,
            priceService.computePriceInRubles(project, feature.getEstimateInDays())))
        .toList();
  }

  /**
   * Updates {@link Feature} in {@link FeatureRepository} with data from {@code request}.
   *
   * @param request   same requirements as in {@link #saveFeature(CreateFeatureDtoRequest, long)}
   * @param projectId must be {@code > 0}
   * @param featureId must be {@code > 0}
   * @return {@link FeatureDto}
   * @throws ProjectCalculatorException if {@link Project} with specified {@code projectId} is not
   *                                    found in {@link ProjectRepository};if {@link Feature} with
   *                                    specified {@code projectId} and {@code featureId} is not
   *                                    found in {@link FeatureRepository}
   */
  @Transactional
  public FeatureDto updateFeature(
      final UpdateFeatureDtoRequest request, final long projectId, final long featureId) {
    final Project project = projectService.getProject(projectId);
    final User user = userService.getCurrentlyAuthenticatedUser();

    projectService.checkIfUserOwnsProject(user, project);

    final Feature feature = getFeature(projectId, featureId);
    final Milestone oldMilestone = feature.getMilestone();
    final Long oldMilestoneId = (oldMilestone != null) ? oldMilestone.getId() : null;
    final Long newMilestoneId = request.getNewMilestoneId();
    final Milestone newMilestone =
        (newMilestoneId != null) ? milestoneService.getMilestone(projectId, newMilestoneId) : null;

    // we need to check if newMilestone belongs to the same project as the feature does

    if (newMilestone != null && newMilestone.getProject().getId() != project.getId()) {
      throw new ProjectCalculatorException(
          ProjectCalculatorError.MILESTONE_IS_NOT_FOUND_BY_ID, "newMilestoneId");
    }

    final var now = LocalDateTime.now(clock);

    log.info("Before update {}", feature);

    // need to subtract feature's estimate BEFORE update from project and oldMilestone

    project.setEstimateInDays(project.getEstimateInDays().subtract(feature.getEstimateInDays()));
    project.setLastUpdatedAt(now);

    if (oldMilestone != null) {
      oldMilestone.setEstimateInDays(
          oldMilestone.getEstimateInDays().subtract(feature.getEstimateInDays()));
      oldMilestone.setLastUpdatedAt(now);
    }

    log.info("Change Feature's milestoneId = {} to milestoneId = {}", oldMilestoneId,
        newMilestoneId);
    feature.setMilestone(newMilestone);

    feature.setTitle(request.getTitle());
    feature.setDescription(request.getDescription());
    feature.setBestCaseEstimateInDays(request.getBestCaseEstimateInDays());
    feature.setMostLikelyEstimateInDays(request.getMostLikelyEstimateInDays());
    feature.setWorstCaseEstimateInDays(request.getWorstCaseEstimateInDays());
    feature.setLastUpdatedAt(now);

    // need to add feature's estimate AFTER update to project and newMilestone

    project.setEstimateInDays(project.getEstimateInDays().add(feature.getEstimateInDays()));

    if (newMilestone != null) {
      newMilestone.setEstimateInDays(
          newMilestone.getEstimateInDays().add(feature.getEstimateInDays()));
      newMilestone.setLastUpdatedAt(now);
    }

    log.info("Updated {}", feature);

    return featureMapper.toFeatureDto(feature);
  }

  /**
   * Deletes {@link Feature} from {@link FeatureRepository}.
   *
   * @param projectId must be {@code > 0}
   * @param featureId must be {@code > 0}
   * @throws ProjectCalculatorException if {@link Project} with specified {@code projectId} is not
   *                                    found in {@link ProjectRepository}; if {@link Feature} with
   *                                    specified {@code projectId} and {@code featureId} is not
   *                                    found in {@link FeatureRepository}
   */
  @Transactional
  public void deleteFeature(final long projectId, final long featureId) {
    final Project project = projectService.getProject(projectId);
    final User user = userService.getCurrentlyAuthenticatedUser();

    projectService.checkIfUserOwnsProject(user, project);

    final Feature feature = getFeature(projectId, featureId);
    final Milestone milestone = feature.getMilestone();

    final var now = LocalDateTime.now(clock);

    log.info("Trying to delete {}", project);

    // need to subtract feature's estimate BEFORE remove from project and milestone

    project.setEstimateInDays(project.getEstimateInDays().subtract(feature.getEstimateInDays()));
    project.setLastUpdatedAt(now);

    if (milestone != null) {
      milestone.setEstimateInDays(
          milestone.getEstimateInDays().subtract(feature.getEstimateInDays()));
      milestone.setLastUpdatedAt(now);
    }

    featureRepository.delete(feature);

    log.info("Deleted {}", project);
  }

  /**
   * Fetches {@link Feature} from {@link FeatureRepository} by {@code projectId} and
   * {@code featureId}.
   *
   * @param projectId must be {@code > 0}
   * @param featureId must be {@code > 0}
   * @return {@link Feature}
   * @throws ProjectCalculatorException if {@link Feature} with specified {@code projectId} and
   *                                    {@code featureId} is not found in {@link FeatureRepository}
   */
  public Feature getFeature(final long projectId, final long featureId) {
    return featureRepository
        .findByIdAndProjectId(featureId, projectId)
        .orElseThrow(
            () ->
                new ProjectCalculatorException(
                    ProjectCalculatorError.FEATURE_IS_NOT_FOUND_BY_ID, "featureId"));
  }
}
