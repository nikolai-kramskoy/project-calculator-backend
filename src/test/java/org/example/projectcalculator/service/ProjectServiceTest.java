package org.example.projectcalculator.service;

import static org.example.projectcalculator.service.utility.ServiceTestHelper.setSecurityContext;
import static org.example.projectcalculator.utility.Asserter.assertProjectsAreEqual;
import static org.example.projectcalculator.utility.TestingData.CLOCK;
import static org.example.projectcalculator.utility.TestingData.PROJECT_MAPPER;
import static org.example.projectcalculator.utility.TestingData.createProject;
import static org.example.projectcalculator.utility.TestingData.createUser;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.example.projectcalculator.model.Rate;
import org.example.projectcalculator.repository.ProjectRepository;
import org.example.projectcalculator.repository.RateRepository;
import org.example.projectcalculator.repository.TeamMemberRepository;
import org.junit.jupiter.api.Assertions;
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

    when(rateRepositoryMock.saveAll(anyList()))
        .thenAnswer(
            (invocation) -> {
              // we need to set project some fields manually because of change
              // from save to saveAll in ProjectService

              final var rates = (List<Rate>) invocation.getArgument(0);
              if (rates.size() == 0) {
                // rates.size() must be > 0
                Assertions.fail();
              }

              rates.stream().findFirst().get().getProject().setId(1L);
              return rates;
            });

    setSecurityContext(creator);

    final var actualProjectDto = projectService.saveProject(createProjectDtoRequest);

    assertProjectsAreEqual(expectedProjectDto, actualProjectDto);
  }
}
