package org.example.projectcalculator.controller;

import static org.example.projectcalculator.Asserter.assertTeamMembersAreEqual;
import static org.example.projectcalculator.TestingData.TEAM_MEMBER_MAPPER;
import static org.example.projectcalculator.TestingData.createProject;
import static org.example.projectcalculator.TestingData.createTeamMember;
import static org.example.projectcalculator.TestingData.createUser;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.example.projectcalculator.controller.utility.JsonConverter;
import org.example.projectcalculator.dto.MilestoneDto;
import org.example.projectcalculator.dto.TeamMemberDto;
import org.example.projectcalculator.service.TeamMemberService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TeamMemberController.class)
@WithMockUser
class TeamMemberControllerTest {

  private static final String TEAM_MEMBERS_API_URL = "/projects/{projectId}/team-members";
  private static final String SPECIFIC_TEAM_MEMBER_API_URL = TEAM_MEMBERS_API_URL + "/{memberId}";

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private TeamMemberService teamMemberServiceMock;

  @Test
  void testCreateTeamMember_validTeamMember_returnTeamMemberDto() throws Exception {
    final var creator = createUser();
    final var project = createProject(creator);
    final var teamMember = createTeamMember(project);
    final var createTeamMemberDtoRequest = TEAM_MEMBER_MAPPER.toCreateUpdateTeamMemberDtoRequest(
        teamMember);
    final var expectedTeamMemberDto = TEAM_MEMBER_MAPPER.toTeamDto(teamMember);

    when(teamMemberServiceMock.saveTeamMember(createTeamMemberDtoRequest,
        project.getId())).thenReturn(expectedTeamMemberDto);

    final var mvcResult =
        mockMvc
            .perform(
                post(TEAM_MEMBERS_API_URL, project.getId())
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonConverter.objectToJson(createTeamMemberDtoRequest)))
            .andReturn();

    final var response = mvcResult.getResponse();

    Assertions.assertEquals(200, response.getStatus());

    final var actualTeamMemberDto = JsonConverter.jsonToObject(
        mvcResult.getResponse().getContentAsString(),
        TeamMemberDto.class);

    assertTeamMembersAreEqual(expectedTeamMemberDto, actualTeamMemberDto);
  }

  @Test
  void testUpdateTeamMember_validTeamMember_returnTeamMemberDto() throws Exception {
    final var creator = createUser();
    final var project = createProject(creator);
    final var teamMember = createTeamMember(project);
    final var updateTeamMemberDtoRequest = TEAM_MEMBER_MAPPER.toCreateUpdateTeamMemberDtoRequest(
        teamMember);
    final var expectedTeamMemberDto = TEAM_MEMBER_MAPPER.toTeamDto(teamMember);

    when(teamMemberServiceMock.updateTeamMember(updateTeamMemberDtoRequest, project.getId(),
        teamMember.getId())).thenReturn(expectedTeamMemberDto);

    final var mvcResult =
        mockMvc
            .perform(
                put(SPECIFIC_TEAM_MEMBER_API_URL, project.getId(), teamMember.getId())
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonConverter.objectToJson(updateTeamMemberDtoRequest)))
            .andReturn();

    final var response = mvcResult.getResponse();

    Assertions.assertEquals(200, response.getStatus());

    final var actualTeamMemberDto = JsonConverter.jsonToObject(
        mvcResult.getResponse().getContentAsString(),
        TeamMemberDto.class);

    assertTeamMembersAreEqual(expectedTeamMemberDto, actualTeamMemberDto);
  }

  @Test
  void testGetAllTeamMembers_validArguments_returnList() throws Exception {
    final var creator = createUser();
    final var project = createProject(creator);
    final var expectedTeamMemberDtos =
        List.of(
            TEAM_MEMBER_MAPPER.toTeamDto(createTeamMember(project)));

    when(teamMemberServiceMock.getAllTeamMembers(project.getId())).thenReturn(
        expectedTeamMemberDtos);

    final var mvcResult =
        mockMvc
            .perform(
                get(TEAM_MEMBERS_API_URL, project.getId())
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8))
            .andReturn();

    final var response = mvcResult.getResponse();

    Assertions.assertEquals(200, response.getStatus());

    final var actualTeamMemberDtos = JsonConverter.jsonToListOfObjects(
        response.getContentAsString(), MilestoneDto.class);

    Assertions.assertEquals(1, actualTeamMemberDtos.size());
  }

  @Test
  void testDeleteTeamMember_validTeamMember_returnTeamMemberDto() throws Exception {
    final long projectId = 1L;
    final long teamMemberId = 1L;

    final var mvcResult =
        mockMvc
            .perform(
                delete(SPECIFIC_TEAM_MEMBER_API_URL, projectId, teamMemberId)
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    assertAll(
        () -> assertEquals(200, mvcResult.getResponse().getStatus()),
        () -> assertEquals(0, mvcResult.getResponse().getContentLength())
    );
  }
}
