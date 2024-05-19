package org.example.projectcalculator.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.projectcalculator.dto.RateDto;
import org.example.projectcalculator.dto.request.UpdateRateDtoRequest;
import org.example.projectcalculator.error.ProjectCalculatorError;
import org.example.projectcalculator.error.ProjectCalculatorException;
import org.example.projectcalculator.mapper.RateMapper;
import org.example.projectcalculator.model.Project;
import org.example.projectcalculator.model.Rate;
import org.example.projectcalculator.model.User;
import org.example.projectcalculator.repository.ProjectRepository;
import org.example.projectcalculator.repository.RateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A {@link Service} that provides operations on {@link Rate}.
 */
@Slf4j
@Service
@AllArgsConstructor
public class RateService {

  private final UserService userService;
  private final ProjectService projectService;

  private final RateRepository rateRepository;

  private final Clock clock;

  private final RateMapper rateMapper;

  /**
   * Returns all {@link RateDto}s from {@link RateRepository}.
   *
   * @param projectId must be {@code > 0}
   * @return {@link List} of {@link RateDto}s
   * @throws ProjectCalculatorException if {@link Project} with specified {@code projectId} is not
   *                                    found in {@link ProjectRepository}
   */
  @Transactional(readOnly = true)
  public List<RateDto> getAllRates(final long projectId) {
    final Project project = projectService.getProject(projectId);
    final User user = userService.getCurrentlyAuthenticatedUser();

    projectService.checkIfUserOwnsProject(user, project);

    final List<Rate> rates = rateRepository.findAllByProjectId(projectId);

    log.info("Get List<Rate> by projectId = {}: {}", projectId, rates);

    return rates.stream().map(rateMapper::toRateDto).toList();
  }

  /**
   * Updates {@link Rate} in {@link RateRepository}.
   *
   * @param request   must be not {@code null}; it's {@code rublesPerHour} must be not null and it
   *                  must be {@code > 0}
   * @param projectId must be {@code > 0}
   * @param rateId    must be {@code > 0}
   * @return {@link RateDto}
   * @throws ProjectCalculatorException if {@link Project} with specified {@code projectId} is not
   *                                    found in {@link ProjectRepository}; if {@link Rate} with
   *                                    specified {@code projectId} and {@code rateId} is not found
   *                                    in {@link RateRepository}
   */
  @Transactional
  public RateDto updateRate(final UpdateRateDtoRequest request, final long projectId,
      final long rateId) {
    final Rate rate = getRate(projectId, rateId);
    final Project project = rate.getProject();
    final User user = userService.getCurrentlyAuthenticatedUser();

    projectService.checkIfUserOwnsProject(user, project);

    log.info("Before update {}", rate);

    rate.setRublesPerHour(request.rublesPerHour());
    project.setLastUpdatedAt(LocalDateTime.now(clock));

    log.info("Updated {}", rate);

    return rateMapper.toRateDto(rate);
  }

  /**
   * Fetches {@link Rate} from {@link RateRepository} by {@code projectId} and {@code rateId}.
   *
   * @param projectId must be {@code > 0}
   * @param rateId    must be {@code > 0}
   * @return {@link Rate}
   * @throws ProjectCalculatorException if {@link Rate} with specified {@code projectId} and
   *                                    {@code rateId} is not found in {@link RateRepository}
   */
  public Rate getRate(final long projectId, final long rateId) {
    return rateRepository
        .findByIdAndProjectId(rateId, projectId)
        .orElseThrow(
            () ->
                new ProjectCalculatorException(
                    ProjectCalculatorError.RATE_IS_NOT_FOUND_BY_ID, "rateId"));
  }
}
