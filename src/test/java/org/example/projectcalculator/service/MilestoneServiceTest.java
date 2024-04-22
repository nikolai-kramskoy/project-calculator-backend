package org.example.projectcalculator.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.example.projectcalculator.service.utility.ServiceTestHelper.setSecurityContext;
import static org.example.projectcalculator.utility.Asserter.assertMilestonesAreEqual;
import static org.example.projectcalculator.utility.TestingData.CLOCK;
import static org.example.projectcalculator.utility.TestingData.NOW;
import static org.example.projectcalculator.utility.TestingData.createMilestone1;
import static org.example.projectcalculator.utility.TestingData.createMilestone2;
import static org.example.projectcalculator.utility.TestingData.createProject;
import static org.example.projectcalculator.utility.TestingData.createUser;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.example.projectcalculator.dto.MilestoneDto;
import org.example.projectcalculator.error.ProjectCalculatorError;
import org.example.projectcalculator.error.ProjectCalculatorException;
import org.example.projectcalculator.mapper.MilestoneMapper;
import org.example.projectcalculator.model.Milestone;
import org.example.projectcalculator.model.Project;
import org.example.projectcalculator.model.User;
import org.example.projectcalculator.repository.MilestoneRepository;

public class MilestoneServiceTest {

  private UserService userServiceMock;
  private ProjectService projectServiceMock;

  private MilestoneRepository milestoneRepositoryMock;

  private static final MilestoneMapper MILESTONE_MAPPER = Mappers.getMapper(MilestoneMapper.class);

  private MilestoneService milestoneService;

  @BeforeEach
  public void initMocks() {
    userServiceMock = mock(UserService.class);
    projectServiceMock = mock(ProjectService.class);

    milestoneRepositoryMock = mock(MilestoneRepository.class);

    milestoneService = new MilestoneService(userServiceMock, projectServiceMock,
        milestoneRepositoryMock, CLOCK, MILESTONE_MAPPER);
  }

  @Test
  public void testCreateMilestone_validMilestone_returnCreatedMilestone() {
    // Arrange

    final User creator = createUser();
    final Project project = createProject(creator);
    final Milestone milestone = createMilestone1(project);
    final var createMilestoneDtoRequest = MILESTONE_MAPPER.toCreateUpdateMilestoneDtoRequest(
        milestone);
    final var expectedMilestoneDto = MILESTONE_MAPPER.toMilestoneDto(milestone);

    setSecurityContext(creator);

    when(projectServiceMock.getProject(eq(project.getId()))).thenReturn(project);

    System.out.println(milestone);

    when(milestoneRepositoryMock.save(any(Milestone.class))).thenReturn(milestone);

    // Act

    final MilestoneDto actualMilestoneDto = milestoneService.saveMilestone(
        createMilestoneDtoRequest, project.getId());

    // Assert

    assertMilestonesAreEqual(expectedMilestoneDto, actualMilestoneDto);
  }

  @Test
  public void testCreateMilestone_wrongProjectId_throwException() {
    // Arrange

    final User creator = createUser();
    Milestone milestone =
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
    final long nonexistentProjectId = 1L;
    final var createMilestoneDtoRequest = MILESTONE_MAPPER.toCreateUpdateMilestoneDtoRequest(
        milestone);

    setSecurityContext(creator);

    when(projectServiceMock.getProject(eq(nonexistentProjectId)))
        .thenThrow(
            new ProjectCalculatorException(
                ProjectCalculatorError.PROJECT_IS_NOT_FOUND_BY_ID, "projectId"));

    // Act, assert

    final var projectCalculatorException =
        Assertions.assertThrows(
            ProjectCalculatorException.class,
            () -> milestoneService.saveMilestone(createMilestoneDtoRequest, nonexistentProjectId));

    Assertions.assertEquals(
        ProjectCalculatorError.PROJECT_IS_NOT_FOUND_BY_ID,
        projectCalculatorException.getProjectCalculatorError());
  }

  @Test
  public void testUpdateMilestone_validMilestone_returnUpdatedMilestone() {
    // Arrange

    final User creator = createUser();
    final Project project = createProject(creator);
    final Milestone milestone = createMilestone1(project);
    final Milestone newMilestone = createMilestone2(project);
    newMilestone.setId(1L);
    final var updateMilestoneDtoRequest = MILESTONE_MAPPER.toCreateUpdateMilestoneDtoRequest(
        newMilestone);
    final var expectedMilestoneDto = MILESTONE_MAPPER.toMilestoneDto(newMilestone);

    setSecurityContext(creator);

    when(projectServiceMock.getProject(eq(project.getId()))).thenReturn(project);

    when(milestoneRepositoryMock.findByIdAndProjectId(eq(milestone.getId()), eq(project.getId())))
        .thenReturn(Optional.of(milestone));

    // Act

    final MilestoneDto actualMilestoneDto = milestoneService.updateMilestone(
        updateMilestoneDtoRequest, project.getId(), milestone.getId());

    // Assert

    assertMilestonesAreEqual(expectedMilestoneDto, actualMilestoneDto);
  }

  @Test
  public void testDeleteMilestone_validMilestone_returnVoid() {
    // Arrange

    final User creator = createUser();
    final Project project = createProject(creator);
    final Milestone milestone = createMilestone1(project);

    setSecurityContext(creator);

    when(projectServiceMock.getProject(eq(project.getId()))).thenReturn(project);

    when(milestoneRepositoryMock.findByIdAndProjectId(eq(milestone.getId()), eq(project.getId())))
        .thenReturn(Optional.of(milestone));

    // Act, assert

    Assertions.assertDoesNotThrow(
        () -> milestoneService.deleteMilestone(project.getId(), milestone.getId()));
  }

  @Test
  public void testDeleteMilestone_wrongMilestoneId_throwException() {
    // Arrange

    final User creator = createUser();
    final Project project = createProject(creator);
    final long nonexistentMilestoneId = 1L;

    setSecurityContext(creator);

    when(projectServiceMock.getProject(eq(project.getId()))).thenReturn(project);

    when(milestoneRepositoryMock.findById(eq(nonexistentMilestoneId))).thenReturn(Optional.empty());

    // Act, assert

    final var projectCalculatorException =
        Assertions.assertThrows(
            ProjectCalculatorException.class,
            () -> milestoneService.deleteMilestone(project.getId(), nonexistentMilestoneId));

    Assertions.assertEquals(
        ProjectCalculatorError.MILESTONE_IS_NOT_FOUND_BY_ID,
        projectCalculatorException.getProjectCalculatorError());
  }

  @Test
  public void testGetAllMilestones_validArguments_returnList() {
    // Arrange

    final User creator = createUser();
    final Project project = createProject(creator);
    final var milestones =
        List.of(
            createMilestone1(project),
            createMilestone2(project));

    setSecurityContext(creator);

    when(projectServiceMock.getProject(eq(project.getId()))).thenReturn(project);

    when(milestoneRepositoryMock.findAllByProjectId(eq(project.getId()))).thenReturn(milestones);

    // Act

    final var milestonesFromService = milestoneService.getAllMilestones(project.getId());

    // Assert

    Assertions.assertNotNull(milestonesFromService);
    Assertions.assertEquals(2, milestonesFromService.size());
  }
}
