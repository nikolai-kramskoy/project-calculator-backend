package org.example.projectcalculator.service;

import static org.example.projectcalculator.Asserter.assertMilestonesAreEqual;
import static org.example.projectcalculator.TestingData.CLOCK;
import static org.example.projectcalculator.TestingData.MILESTONE_MAPPER;
import static org.example.projectcalculator.TestingData.NOW;
import static org.example.projectcalculator.TestingData.createMilestone1;
import static org.example.projectcalculator.TestingData.createMilestone2;
import static org.example.projectcalculator.TestingData.createProject;
import static org.example.projectcalculator.TestingData.createUser;
import static org.example.projectcalculator.service.utility.ServiceTestHelper.setSecurityContext;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.example.projectcalculator.error.ProjectCalculatorError;
import org.example.projectcalculator.error.ProjectCalculatorException;
import org.example.projectcalculator.model.Milestone;
import org.example.projectcalculator.repository.MilestoneRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MilestoneServiceTest {

  private UserService userServiceMock;
  private ProjectService projectServiceMock;
  private PriceService priceServiceMock;

  private MilestoneRepository milestoneRepositoryMock;

  private MilestoneService milestoneService;

  @BeforeEach
  public void initMocks() {
    userServiceMock = mock(UserService.class);
    projectServiceMock = mock(ProjectService.class);
    priceServiceMock = mock(PriceService.class);

    milestoneRepositoryMock = mock(MilestoneRepository.class);

    milestoneService = new MilestoneService(userServiceMock, projectServiceMock, priceServiceMock,
        milestoneRepositoryMock, CLOCK, MILESTONE_MAPPER);
  }

  @Test
  void testCreateMilestone_validMilestone_returnCreatedMilestone() {
    final var creator = createUser();
    final var project = createProject(creator);
    final var milestone = createMilestone1(project);
    final var createMilestoneDtoRequest = MILESTONE_MAPPER.toCreateUpdateMilestoneDtoRequest(
        milestone);
    final var expectedMilestoneDto = MILESTONE_MAPPER.toMilestoneDto(milestone);

    when(projectServiceMock.getProject(project.getId())).thenReturn(project);

    when(milestoneRepositoryMock.save(any(Milestone.class))).thenReturn(milestone);

    setSecurityContext(creator);

    final var actualMilestoneDto = milestoneService.saveMilestone(createMilestoneDtoRequest,
        project.getId());

    assertMilestonesAreEqual(expectedMilestoneDto, actualMilestoneDto);
  }

  @Test
  void testCreateMilestone_wrongProjectId_throwException() {
    final var creator = createUser();
    var milestone =
        new Milestone(
            0L,
            null,
            "Basic API",
            "Basic API blah...",
            NOW.plusDays(5),
            NOW.plusDays(10),
            BigDecimal.ZERO,
            null,
            null);
    final var nonexistentProjectId = 1L;
    final var createMilestoneDtoRequest = MILESTONE_MAPPER.toCreateUpdateMilestoneDtoRequest(
        milestone);

    when(projectServiceMock.getProject(nonexistentProjectId)).thenThrow(
        new ProjectCalculatorException(ProjectCalculatorError.PROJECT_IS_NOT_FOUND_BY_ID,
            "projectId"));

    setSecurityContext(creator);

    final var projectCalculatorException =
        Assertions.assertThrows(
            ProjectCalculatorException.class,
            () -> milestoneService.saveMilestone(createMilestoneDtoRequest, nonexistentProjectId));

    Assertions.assertEquals(
        ProjectCalculatorError.PROJECT_IS_NOT_FOUND_BY_ID,
        projectCalculatorException.getProjectCalculatorError());
  }

  @Test
  void testUpdateMilestone_validMilestone_returnUpdatedMilestone() {
    final var creator = createUser();
    final var project = createProject(creator);
    final var milestone = createMilestone1(project);
    final var newMilestone = createMilestone2(project);
    newMilestone.setId(1L);
    final var updateMilestoneDtoRequest = MILESTONE_MAPPER.toCreateUpdateMilestoneDtoRequest(
        newMilestone);
    final var expectedMilestoneDto = MILESTONE_MAPPER.toMilestoneDto(newMilestone);

    when(projectServiceMock.getProject(project.getId())).thenReturn(project);

    when(milestoneRepositoryMock.findByIdAndProjectId(milestone.getId(),
        project.getId())).thenReturn(Optional.of(milestone));

    setSecurityContext(creator);

    final var actualMilestoneDto = milestoneService.updateMilestone(updateMilestoneDtoRequest,
        project.getId(), milestone.getId());

    assertMilestonesAreEqual(expectedMilestoneDto, actualMilestoneDto);
  }

  @Test
  void testDeleteMilestone_validMilestone_returnVoid() {
    final var creator = createUser();
    final var project = createProject(creator);
    final var milestone = createMilestone1(project);

    when(projectServiceMock.getProject(project.getId())).thenReturn(project);

    when(milestoneRepositoryMock.findByIdAndProjectId(milestone.getId(), eq(project.getId())))
        .thenReturn(Optional.of(milestone));

    setSecurityContext(creator);

    Assertions.assertDoesNotThrow(
        () -> milestoneService.deleteMilestone(project.getId(), milestone.getId()));
  }

  @Test
  void testDeleteMilestone_wrongMilestoneId_throwException() {
    final var creator = createUser();
    final var project = createProject(creator);
    final long nonexistentMilestoneId = 1L;

    when(projectServiceMock.getProject(project.getId())).thenReturn(project);

    when(milestoneRepositoryMock.findById(nonexistentMilestoneId)).thenReturn(Optional.empty());

    setSecurityContext(creator);

    final var projectCalculatorException =
        Assertions.assertThrows(
            ProjectCalculatorException.class,
            () -> milestoneService.deleteMilestone(project.getId(), nonexistentMilestoneId));

    Assertions.assertEquals(
        ProjectCalculatorError.MILESTONE_IS_NOT_FOUND_BY_ID,
        projectCalculatorException.getProjectCalculatorError());
  }

  @Test
  void testGetAllMilestones_validArguments_returnList() {
    final var creator = createUser();
    final var project = createProject(creator);
    final var milestones =
        List.of(
            createMilestone1(project),
            createMilestone2(project));

    when(projectServiceMock.getProject(project.getId())).thenReturn(project);

    when(milestoneRepositoryMock.findAllByProjectId(project.getId())).thenReturn(milestones);

    setSecurityContext(creator);

    final var milestonesFromService = milestoneService.getAllMilestones(project.getId());

    Assertions.assertNotNull(milestonesFromService);
    Assertions.assertEquals(2, milestonesFromService.size());
  }
}
