package org.example.projectcalculator.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.example.projectcalculator.controller.utility.JsonConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.example.projectcalculator.dto.TeamMemberDto;
import org.example.projectcalculator.dto.error.ErrorDto;
import org.example.projectcalculator.dto.error.ErrorDtoResponse;
import org.example.projectcalculator.dto.request.CreateUpdateTeamMemberDtoRequest;
import org.example.projectcalculator.mapper.TeamMemberMapper;
import org.example.projectcalculator.model.Position;
import org.example.projectcalculator.model.Project;
import org.example.projectcalculator.model.TeamMember;
import org.example.projectcalculator.model.User;
import org.example.projectcalculator.service.TeamMemberService;
import org.example.projectcalculator.utility.TestingData;

@WebMvcTest(TeamMemberController.class)
@ComponentScan(basePackageClasses = TeamMemberMapper.class)
@WithMockUser
class TeamMemberControllerTest {

  public static final long projectId = 1L;
  private static final long memberId = 23L;
  private static final String PROJECT_API_URL = "/projects";
  private static final String TEAM_API_URL = PROJECT_API_URL + "/" + projectId + "/team-members";
  private static final String SPECIFIC_TEAM_API_URL = TEAM_API_URL + "/" + memberId;
  private static final BigDecimal NUMBER_OF_TEAM_MEMBERS = new BigDecimal("1.75");

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  private TeamMemberMapper teamMemberMapper;

  @MockBean
  private TeamMemberService teamMemberService;

  @Test
  void testCreateSuccessful() throws Exception {
    final var creator = TestingData.createUser();
    final var project = TestingData.createProject(creator);

    final var request =
        new CreateUpdateTeamMemberDtoRequest(Position.DEVOPS_ENGINEER.name(),
            NUMBER_OF_TEAM_MEMBERS);

    final var teamMember = createTeamMember();
    final var expectedTeamMemberDto = teamMemberMapper.toTeamDto(teamMember);

    when(teamMemberService.saveTeamMember(request, project.getId())).thenReturn(expectedTeamMemberDto);

    final var result = mockMvc.perform(post(TEAM_API_URL, project.getId())
            .with(csrf())
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonConverter.objectToJson(request)))
        .andReturn();

    final var teamMemberDto = JsonConverter.jsonToObject(result.getResponse().getContentAsString(), TeamMemberDto.class);

    assertAll(
        () -> assertEquals(200, result.getResponse().getStatus()),
        () -> assertTrue(teamMemberDto.id() > 0),
        () -> assertEquals(teamMember.getPosition().name(), teamMemberDto.position()),
        () -> assertEquals(teamMember.getNumberOfTeamMembers(), teamMemberDto.numberOfTeamMembers())
    );
  }

  @Test
  void testCreateFailureByAllRequestFields() throws Exception {
    final var numberOfTeamMembers = new BigDecimal("-2.25");
    final var request = new CreateUpdateTeamMemberDtoRequest(null, numberOfTeamMembers);

    final var result = mockMvc.perform(post(TEAM_API_URL)
            .with(csrf())
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonConverter.objectToJson(request)))
        .andReturn();

    final var response = JsonConverter.jsonToObject(result.getResponse().getContentAsString(), ErrorDtoResponse.class);

    assertAll(
        () -> assertEquals(400, result.getResponse().getStatus()),
        () -> assertEquals(2, response.errors().size())
    );
  }

  @Test
  void testUpdateSuccessful() throws Exception {
    final var teamMember = createTeamMember();
    final var request = new CreateUpdateTeamMemberDtoRequest(teamMember.getPosition().name(),
        NUMBER_OF_TEAM_MEMBERS);
    final var expectedTeamMemberDto = teamMemberMapper.toTeamDto(teamMember);

    when(teamMemberService.updateTeamMember(request, teamMember.getProject().getId(), teamMember.getId())).thenReturn(expectedTeamMemberDto);

    final var result = mockMvc.perform(put(SPECIFIC_TEAM_API_URL)
            .with(csrf())
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonConverter.objectToJson(request)))
        .andReturn();

    final var teamMemberDto = JsonConverter.jsonToObject(result.getResponse().getContentAsString(), TeamMemberDto.class);

    assertAll(
        () -> assertEquals(200, result.getResponse().getStatus()),
        () -> assertTrue(teamMemberDto.id() > 0),
        () -> assertEquals(teamMember.getPosition().name(), teamMemberDto.position()),
        () -> assertEquals(teamMember.getNumberOfTeamMembers(), teamMemberDto.numberOfTeamMembers())
    );
  }

  @Test
  void testUpdateFailure() throws Exception {
    final var numberOfTeamMembers = new BigDecimal("-2.25");
    final var request = new CreateUpdateTeamMemberDtoRequest(null, numberOfTeamMembers);

    final var result = mockMvc.perform(put(SPECIFIC_TEAM_API_URL)
            .with(csrf())
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonConverter.objectToJson(request)))
        .andReturn();

    final var errorDto = JsonConverter.jsonToObject(result.getResponse().getContentAsString(), ErrorDto.class);

    assertAll(
        () -> assertEquals(400, result.getResponse().getStatus()),
        () -> assertNotNull(errorDto.errorCode()),
        () -> assertNotNull(errorDto.errorMessage()),
        () -> assertNotNull(errorDto.fieldWithError())
    );
  }

  @Test
  void testGetSuccessful() throws Exception {
    final var creator = TestingData.createUser();
    final var project = TestingData.createProject(creator);
    final var teamMember = createTeamMember();
    final var request = new CreateUpdateTeamMemberDtoRequest(teamMember.getPosition().name(), teamMember.getNumberOfTeamMembers());

    final var result = mockMvc.perform(get(TEAM_API_URL, project.getId())
            .with(csrf())
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonConverter.objectToJson(request)))
        .andReturn();

    final var teamMemberDtos = JsonConverter.jsonToListOfObjects(result.getResponse().getContentAsString(), TeamMemberDto.class);

    assertAll(
        () -> assertEquals(200, result.getResponse().getStatus()),
        () -> assertEquals(0, teamMemberDtos.size())
    );
  }

  @Test
  void testDeleteSuccessful() throws Exception {
    final var result = mockMvc.perform(delete(SPECIFIC_TEAM_API_URL, projectId, memberId)
            .with(csrf())
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    assertAll(
        () -> assertEquals(200, result.getResponse().getStatus()),
        () -> assertEquals(0, result.getResponse().getContentLength())
    );
  }

  private TeamMember createTeamMember() {
    return new TeamMember(
        memberId,
        Position.SENIOR_DEVELOPER,
        NUMBER_OF_TEAM_MEMBERS,
        TestingData.createProject(TestingData.createUser()));
  }
}
