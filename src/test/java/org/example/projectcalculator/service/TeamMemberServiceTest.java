package org.example.projectcalculator.service;

import static org.example.projectcalculator.TestingData.CLOCK;
import static org.example.projectcalculator.TestingData.TEAM_MEMBER_MAPPER;
import static org.example.projectcalculator.TestingData.createProject;
import static org.example.projectcalculator.TestingData.createTeamMember;
import static org.example.projectcalculator.TestingData.createUser;
import static org.example.projectcalculator.service.utility.ServiceTestHelper.setSecurityContext;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.example.projectcalculator.model.TeamMember;
import org.example.projectcalculator.repository.TeamMemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TeamMemberServiceTest {

  private UserService userServiceMock;
  private ProjectService projectServiceMock;

  private TeamMemberRepository teamMemberRepositoryMock;

  private TeamMemberService teamMemberService;

  @BeforeEach
  public void initMocks() {
    userServiceMock = mock(UserService.class);
    projectServiceMock = mock(ProjectService.class);

    teamMemberRepositoryMock = mock(TeamMemberRepository.class);

    teamMemberService =
        new TeamMemberService(
            userServiceMock,
            projectServiceMock,
            teamMemberRepositoryMock,
            CLOCK,
            TEAM_MEMBER_MAPPER);
  }

  @Test
  void testSaveTeamMember_validTeamMember_returnCreatedTeamMember() {
    final var creator = createUser();
    final var project = createProject(creator);
    final var teamMember = createTeamMember(project);
    final var createUpdateTeamMemberDtoRequest = TEAM_MEMBER_MAPPER.toCreateUpdateTeamMemberDtoRequest(
        teamMember);
    final var expectedTeamMemberDto = TEAM_MEMBER_MAPPER.toTeamDto(teamMember);

    when(projectServiceMock.getProject(project.getId())).thenReturn(project);

    when(teamMemberRepositoryMock.findById(teamMember.getId())).thenReturn(Optional.of(teamMember));
    when(teamMemberRepositoryMock.save(any(TeamMember.class))).thenReturn(teamMember);

    setSecurityContext(creator);

    final var actualTeamMemberDto = teamMemberService.saveTeamMember(
        createUpdateTeamMemberDtoRequest, project.getId());

    Assertions.assertEquals(expectedTeamMemberDto.id(), actualTeamMemberDto.id());
    Assertions.assertEquals(expectedTeamMemberDto.position(), actualTeamMemberDto.position());
    Assertions.assertEquals(expectedTeamMemberDto.numberOfTeamMembers(),
        actualTeamMemberDto.numberOfTeamMembers());
  }
}
