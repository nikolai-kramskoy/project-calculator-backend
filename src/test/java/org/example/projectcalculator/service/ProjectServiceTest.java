package org.example.projectcalculator.service;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.example.projectcalculator.utility.Asserter.assertProjectsAreEqual;
import static org.example.projectcalculator.utility.TestingData.CLOCK;
import static org.example.projectcalculator.utility.TestingData.createProject;
import static org.example.projectcalculator.utility.TestingData.createUser;

import java.util.List;
import org.example.projectcalculator.service.utility.ServiceTestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.example.projectcalculator.dto.ProjectDto;
import org.example.projectcalculator.mapper.ProjectMapper;
import org.example.projectcalculator.model.Project;
import org.example.projectcalculator.model.Rate;
import org.example.projectcalculator.model.User;
import org.example.projectcalculator.repository.ProjectRepository;
import org.example.projectcalculator.repository.RateRepository;
import org.example.projectcalculator.repository.TeamMemberRepository;

public class ProjectServiceTest {

  private UserService userServiceMock;

  private ProjectRepository projectRepositoryMock;
  private RateRepository rateRepositoryMock;
  private TeamMemberRepository teamMemberRepositoryMock;

  private static final ProjectMapper PROJECT_MAPPER = Mappers.getMapper(ProjectMapper.class);

  private ProjectService projectService;

  @BeforeEach
  public void initMocks() {
    userServiceMock = mock(UserService.class);

    projectRepositoryMock = mock(ProjectRepository.class);
    rateRepositoryMock = mock(RateRepository.class);
    teamMemberRepositoryMock = mock(TeamMemberRepository.class);

    projectService =
        new ProjectService(
            userServiceMock, projectRepositoryMock, rateRepositoryMock, teamMemberRepositoryMock,
            CLOCK,
            PROJECT_MAPPER);
  }

  @Test
  public void testCreateProject_validProject_returnCreatedProject() {
    // Arrange

    final User creator = createUser();
    final Project project = createProject(creator);
    final var createProjectDtoRequest = PROJECT_MAPPER.toCreateProjectDtoRequest(project);
    final var expectedProjectDto = PROJECT_MAPPER.toProjectDto(project);

    ServiceTestHelper.setSecurityContext(creator);

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

    // Act

    final ProjectDto actualProjectDto = projectService.saveProject(createProjectDtoRequest);

    // Assert

    assertProjectsAreEqual(expectedProjectDto, actualProjectDto);
  }
}
