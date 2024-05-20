package org.example.projectcalculator.service;

import static org.example.projectcalculator.Asserter.assertProjectsAreEqual;
import static org.example.projectcalculator.TestingData.CLOCK;
import static org.example.projectcalculator.TestingData.PROJECT_MAPPER;
import static org.example.projectcalculator.TestingData.createProject;
import static org.example.projectcalculator.TestingData.createUser;
import static org.example.projectcalculator.service.utility.ServiceTestHelper.setSecurityContext;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.example.projectcalculator.model.Project;
import org.example.projectcalculator.repository.ProjectRepository;
import org.example.projectcalculator.repository.RateRepository;
import org.example.projectcalculator.repository.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProjectServiceTest {

  private UserService userServiceMock;
  private PriceService priceServiceMock;

  private ProjectRepository projectRepositoryMock;
  private RateRepository rateRepositoryMock;
  private TeamMemberRepository teamMemberRepositoryMock;

  private ProjectService projectService;

  @BeforeEach
  public void initMocks() {
    userServiceMock = mock(UserService.class);
    priceServiceMock = mock(PriceService.class);

    projectRepositoryMock = mock(ProjectRepository.class);
    rateRepositoryMock = mock(RateRepository.class);
    teamMemberRepositoryMock = mock(TeamMemberRepository.class);

    projectService =
        new ProjectService(
            userServiceMock,
            priceServiceMock,
            projectRepositoryMock,
            rateRepositoryMock,
            teamMemberRepositoryMock,
            CLOCK,
            PROJECT_MAPPER);
  }

  @Test
  void testCreateProject_validProject_returnCreatedProject() {
    final var creator = createUser();
    final var project = createProject(creator);
    final var createProjectDtoRequest = PROJECT_MAPPER.toCreateProjectDtoRequest(project);
    final var expectedProjectDto = PROJECT_MAPPER.toProjectDto(project);

    when(userServiceMock.getCurrentlyAuthenticatedUser()).thenReturn(creator);

    when(projectRepositoryMock.save(any(Project.class))).thenReturn(project);

    // No need to mock saveAll methods

    setSecurityContext(creator);

    final var actualProjectDto = projectService.createProject(createProjectDtoRequest);

    assertProjectsAreEqual(expectedProjectDto, actualProjectDto);
  }
}
