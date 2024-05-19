package org.example.projectcalculator.controller;

import static org.example.projectcalculator.Asserter.assertMilestonesAreEqual;
import static org.example.projectcalculator.Asserter.assertValidationError;
import static org.example.projectcalculator.TestingData.MILESTONE_MAPPER;
import static org.example.projectcalculator.TestingData.NOW;
import static org.example.projectcalculator.TestingData.createMilestone1;
import static org.example.projectcalculator.TestingData.createMilestone2;
import static org.example.projectcalculator.TestingData.createProject;
import static org.example.projectcalculator.TestingData.createUser;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.validation.constraints.NotBlank;
import org.example.projectcalculator.controller.utility.JsonConverter;
import org.example.projectcalculator.dto.MilestoneDto;
import org.example.projectcalculator.dto.error.ErrorDtoResponse;
import org.example.projectcalculator.dto.request.CreateUpdateMilestoneDtoRequest;
import org.example.projectcalculator.dto.request.validation.annotation.MilestoneDates;
import org.example.projectcalculator.service.MilestoneService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MilestoneController.class)
@WithMockUser
class MilestoneControllerTest {

  private static final String MILESTONES_API_URL = "/projects/{projectId}/milestones";
  private static final String SPECIFIC_MILESTONE_API_URL = MILESTONES_API_URL + "/{milestoneId}";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MilestoneService milestoneServiceMock;

  @Test
  void testCreateMilestone_validMilestone_returnMilestoneDto() throws Exception {
    final var creator = createUser();
    final var project = createProject(creator);
    final var milestone = createMilestone1(project);
    final var createMilestoneDtoRequest = MILESTONE_MAPPER.toCreateUpdateMilestoneDtoRequest(
        milestone);
    final var expectedMilestoneDto = MILESTONE_MAPPER.toMilestoneDto(milestone);

    when(milestoneServiceMock.saveMilestone(createMilestoneDtoRequest, project.getId())).thenReturn(
        expectedMilestoneDto);

    final var mvcResult =
        mockMvc
            .perform(
                post(MILESTONES_API_URL, project.getId())
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonConverter.objectToJson(createMilestoneDtoRequest)))
            .andReturn();

    final var response = mvcResult.getResponse();

    Assertions.assertEquals(200, response.getStatus());

    final var actualMilestoneDto = JsonConverter.jsonToObject(response.getContentAsString(),
        MilestoneDto.class);

    assertMilestonesAreEqual(expectedMilestoneDto, actualMilestoneDto);
  }

  @Test
  void testCreateMilestone_invalidMilestoneDates_returnErrorDtoResponse() throws Exception {
    final var createUpdateMilestoneDtoRequest =
        new CreateUpdateMilestoneDtoRequest(null, null, NOW.plusMonths(1), NOW);

    final var mvcResult =
        mockMvc
            .perform(
                post(MILESTONES_API_URL, 1L)
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonConverter.objectToJson(createUpdateMilestoneDtoRequest)))
            .andReturn();

    final var response = mvcResult.getResponse();

    Assertions.assertEquals(400, response.getStatus());

    final var errorDtoResponse =
        JsonConverter.jsonToObject(response.getContentAsString(), ErrorDtoResponse.class);

    // Must be 3 validation errors
    Assertions.assertEquals(3, errorDtoResponse.errors().size());

    final String notBlankErrorCode = NotBlank.class.getSimpleName();
    final String notBlankErrorMessage = "must not be blank";

    final var errors = errorDtoResponse.errors();

    assertValidationError(errors, "title", notBlankErrorCode, notBlankErrorMessage);
    assertValidationError(errors, "description", notBlankErrorCode, notBlankErrorMessage);
    assertValidationError(
        errors,
        "createUpdateMilestoneDtoRequest",
        MilestoneDates.class.getSimpleName(),
        "milestone dates are invalid");
  }

  @Test
  void testCreateMilestone_invalidProjectId_returnErrorDtoResponse() throws Exception {
    final var createUpdateMilestoneDtoRequest =
        new CreateUpdateMilestoneDtoRequest(
            "Basic API", "Basic API blah...", NOW.plusMonths(1), NOW.plusMonths(2));

    final var mvcResult =
        mockMvc
            .perform(
                post(MILESTONES_API_URL, 0L)
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonConverter.objectToJson(createUpdateMilestoneDtoRequest)))
            .andReturn();

    final var response = mvcResult.getResponse();

    Assertions.assertEquals(400, response.getStatus());

    final var errorDtoResponse =
        JsonConverter.jsonToObject(response.getContentAsString(), ErrorDtoResponse.class);

    // Must be 1 validation error
    Assertions.assertEquals(1, errorDtoResponse.errors().size());

    final var errors = errorDtoResponse.errors();

    assertValidationError(errors, "projectId", "Min", "must be greater than or equal to 1");
  }

  @Test
  void testGetAllMilestones_validArguments_returnList() throws Exception {
    final var creator = createUser();
    final var project = createProject(creator);
    final var expectedMilestoneDtos =
        List.of(
            MILESTONE_MAPPER.toMilestoneDto(createMilestone1(project)),
            MILESTONE_MAPPER.toMilestoneDto(createMilestone2(project)));

    when(milestoneServiceMock.getAllMilestones(project.getId())).thenReturn(expectedMilestoneDtos);

    final var mvcResult =
        mockMvc
            .perform(
                get(MILESTONES_API_URL, project.getId())
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8))
            .andReturn();

    final var response = mvcResult.getResponse();

    Assertions.assertEquals(200, response.getStatus());

    final var actualMilestoneDtos = JsonConverter.jsonToListOfObjects(response.getContentAsString(),
        MilestoneDto.class);

    Assertions.assertEquals(2, actualMilestoneDtos.size());
  }

  @Test
  void testUpdateMilestone_validMilestone_returnMilestoneDto() throws Exception {
    final var creator = createUser();
    final var project = createProject(creator);
    final var updatedMilestone = createMilestone1(project);
    final var updateMilestoneDtoRequest = MILESTONE_MAPPER.toCreateUpdateMilestoneDtoRequest(
        updatedMilestone);
    final var expectedMilestoneDto = MILESTONE_MAPPER.toMilestoneDto(updatedMilestone);

    when(milestoneServiceMock.updateMilestone(updateMilestoneDtoRequest, project.getId(),
        updatedMilestone.getId())).thenReturn(expectedMilestoneDto);

    final var mvcResult =
        mockMvc
            .perform(
                put(SPECIFIC_MILESTONE_API_URL, project.getId(), updatedMilestone.getId())
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonConverter.objectToJson(updateMilestoneDtoRequest)))
            .andReturn();

    final var response = mvcResult.getResponse();

    Assertions.assertEquals(200, response.getStatus());

    final var actualMilestoneDto = JsonConverter.jsonToObject(response.getContentAsString(),
        MilestoneDto.class);

    assertMilestonesAreEqual(expectedMilestoneDto, actualMilestoneDto);
  }

  @Test
  void testDeleteMilestone_validMilestone_returnMilestoneDto() throws Exception {
    final long projectId = 1L;
    final long milestoneId = 1L;

    final var mvcResult =
        mockMvc
            .perform(
                delete(SPECIFIC_MILESTONE_API_URL, projectId, milestoneId)
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8))
            .andReturn();

    final var response = mvcResult.getResponse();

    Assertions.assertEquals(200, response.getStatus());
    Assertions.assertEquals(0, response.getContentLength());
  }
}
