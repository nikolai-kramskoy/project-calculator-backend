package org.example.projectcalculator.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.example.projectcalculator.error.ProjectCalculatorException;
import org.example.projectcalculator.model.Position;
import org.example.projectcalculator.model.Project;
import org.example.projectcalculator.model.TeamMember;
import org.example.projectcalculator.model.User;
import org.example.projectcalculator.repository.ProjectRepository;
import org.example.projectcalculator.repository.TeamMemberRepository;
import org.example.projectcalculator.repository.UserRepository;
import org.example.projectcalculator.service.utility.ServiceTestHelper;
import org.example.projectcalculator.utility.TestingData;

public class TeamMemberServiceTest {

  private static final long projectId = 12L;
  private final long memberId = 1L;
  private final double degreeOfInvolvement = 2.5;


  @Test
  public void testSuccessfulTeamCreate() {
    final User creator = TestingData.createUser();
    final Project project = TestingData.createProject(creator);

    TeamMember teamMember = TestingData.createTeamMember(project);
    final TeamMember memberFromRepository = TestingData.createTeamMember(project);

    final var projectServiceMock = mock(ProjectService.class);

    final var userRepositoryMock = mock(UserRepository.class);
    final var projectRepositoryMock = mock(ProjectRepository.class);
    final var teamRepositoryMock = mock(TeamMemberRepository.class);
    ServiceTestHelper.setSecurityContext(creator);

    when(projectServiceMock.getProject(anyLong())).thenReturn(project);

    when(userRepositoryMock.findByLogin(anyString())).thenReturn(Optional.of(creator));
    when(projectRepositoryMock.findById(anyLong())).thenReturn(Optional.of(project));
    when(teamRepositoryMock.findById(anyLong())).thenReturn(Optional.of(teamMember));
    when(teamRepositoryMock.save(teamMember)).thenReturn(memberFromRepository);

    final var teamService = new TeamMemberService(
        userRepositoryMock, projectServiceMock, teamRepositoryMock);

    TeamMember member = teamService.saveTeamMember(projectId, teamMember);

    Assertions.assertTrue(member.getId() > 0);
    Assertions.assertEquals(member.getPosition(), member.getPosition());
    Assertions.assertEquals(member.getNumberOfTeamMembers(), member.getNumberOfTeamMembers());
  }

  @Test
  public void testFailureTeamCreate() {
    final User creator = TestingData.createUser();
    final Project project = TestingData.createProject(creator);

    TeamMember teamMember = TestingData.createTeamMember(project);

    final var projectServiceMock = mock(ProjectService.class);

    final var userRepositoryMock = mock(UserRepository.class);
    final var projectRepositoryMock = mock(ProjectRepository.class);
    final var teamRepositoryMock = mock(TeamMemberRepository.class);
    ServiceTestHelper.setSecurityContext(creator);

    when(projectServiceMock.getProject(anyLong())).thenReturn(project);

    when(userRepositoryMock.findByLogin(anyString())).thenReturn(Optional.of(creator));
    when(projectRepositoryMock.findById(anyLong())).thenReturn(Optional.of(project));
    when(teamRepositoryMock.findById(anyLong())).thenReturn(Optional.of(teamMember));
    when(teamRepositoryMock.save(teamMember)).thenThrow(ProjectCalculatorException.class);

    final var teamService = new TeamMemberService(
        projectServiceMock, teamRepositoryMock);

    Assertions.assertThrows(ProjectCalculatorException.class,
        () -> teamService.saveTeamMember(projectId, teamMember));
  }


  @Test
  public void testSuccessfulTeamUpdate() {
    final User creator = TestingData.createUser();
    final Project project = TestingData.createProject(creator);

    final TeamMember memberFromRepository = TestingData.createTeamMember(project);

    final var projectServiceMock = mock(ProjectService.class);

    final var userRepositoryMock = mock(UserRepository.class);
    final var projectRepositoryMock = mock(ProjectRepository.class);
    final var teamRepositoryMock = mock(TeamMemberRepository.class);
    ServiceTestHelper.setSecurityContext(creator);

    when(projectServiceMock.getProject(anyLong())).thenReturn(project);

    when(userRepositoryMock.findByLogin(anyString())).thenReturn(Optional.of(creator));
    when(projectRepositoryMock.findById(anyLong())).thenReturn(Optional.of(project));
    when(teamRepositoryMock.findByIdAndProjectId(anyLong(), anyLong())).thenReturn(
        memberFromRepository);

    final var teamService = new TeamMemberService(
        projectServiceMock, teamRepositoryMock);

    TeamMember member = teamService.updateTeamMember(projectId, memberId, degreeOfInvolvement);

    Assertions.assertTrue(member.getId() > 0);
    Assertions.assertEquals(member.getPosition(), member.getPosition());
    Assertions.assertEquals(member.getNumberOfTeamMembers(), member.getNumberOfTeamMembers());
  }

  @Test
  public void testSuccessfulTeamGetByProject() {
    final User creator = TestingData.createUser();
    final Project project = TestingData.createProject(creator);

    List<TeamMember> team = new ArrayList<>();
    long id = 1;
    for (Position position : Position.values()) {
      team.add(new TeamMember(
          id++,
          position,
          degreeOfInvolvement,
          project));
    }

    final var projectServiceMock = mock(ProjectService.class);

    final var userRepositoryMock = mock(UserRepository.class);
    final var projectRepositoryMock = mock(ProjectRepository.class);
    final var teamRepositoryMock = mock(TeamMemberRepository.class);
    ServiceTestHelper.setSecurityContext(creator);

    when(projectServiceMock.getProject(anyLong())).thenReturn(project);

    when(userRepositoryMock.findByLogin(anyString())).thenReturn(Optional.of(creator));
    when(projectRepositoryMock.findById(anyLong())).thenReturn(Optional.of(project));
    when(teamRepositoryMock.findAllByProjectId(anyLong())).thenReturn(team);

    final var teamService = new TeamMemberService(
        projectServiceMock, teamRepositoryMock);

    List<TeamMember> result = teamService.getAllTeamMembers(projectId);

    Assertions.assertTrue(result.size() > 0);
    Assertions.assertEquals(result.size(), Position.values().length);
  }


  @Test
  public void testSuccessfulTeamDelete() {
    final User creator = TestingData.createUser();
    final Project project = TestingData.createProject(creator);

    final TeamMember teamMember = TestingData.createTeamMember(project);
    final TeamMember memberFromRepository = TestingData.createTeamMember(project);

    final var projectServiceMock = mock(ProjectService.class);

    final var userRepositoryMock = mock(UserRepository.class);
    final var projectRepositoryMock = mock(ProjectRepository.class);
    final var teamRepositoryMock = mock(TeamMemberRepository.class);
    ServiceTestHelper.setSecurityContext(creator);

    when(projectServiceMock.getProject(anyLong())).thenReturn(project);

    when(userRepositoryMock.findByLogin(anyString())).thenReturn(Optional.of(creator));
    when(projectRepositoryMock.findById(anyLong())).thenReturn(Optional.of(project));
    when(teamRepositoryMock.findByIdAndProjectId(anyLong(), anyLong())).thenReturn(
        teamMember);
    when(teamRepositoryMock.save(teamMember)).thenReturn(memberFromRepository);

    final var teamService = new TeamMemberService(
        projectServiceMock, teamRepositoryMock);

    Assertions.assertDoesNotThrow(
        () -> teamService.deleteTeamMember(project.getId(), teamMember.getId()));
  }
}
