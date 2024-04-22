package org.example.projectcalculator.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.example.projectcalculator.utility.Asserter.assertMilestonesAreEqual;
import static org.example.projectcalculator.utility.Asserter.assertValidationError;
import static org.example.projectcalculator.utility.TestingData.NOW;
import static org.example.projectcalculator.utility.TestingData.createMilestone1;
import static org.example.projectcalculator.utility.TestingData.createMilestone2;
import static org.example.projectcalculator.utility.TestingData.createProject;
import static org.example.projectcalculator.utility.TestingData.createUser;

import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.validation.constraints.NotBlank;
import org.example.projectcalculator.controller.utility.JsonConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.example.projectcalculator.dto.MilestoneDto;
import org.example.projectcalculator.dto.error.ErrorDtoResponse;
import org.example.projectcalculator.dto.request.CreateUpdateMilestoneDtoRequest;
import org.example.projectcalculator.dto.request.validation.annotation.MilestoneDates;
import org.example.projectcalculator.mapper.MilestoneMapper;
import org.example.projectcalculator.model.Milestone;
import org.example.projectcalculator.model.Project;
import org.example.projectcalculator.model.User;
import org.example.projectcalculator.service.MilestoneService;

@WebMvcTest(MilestoneController.class)
@ComponentScan(basePackageClasses = MilestoneMapper.class)
@WithMockUser
public class MilestoneControllerTest {

  private static final String PROJECT_API_URL = "/projects";
  private static final String MILESTONE_API_URL = PROJECT_API_URL + "/{projectId}/milestones";
  private static final String SPECIFIC_MILESTONE_API_URL = MILESTONE_API_URL + "/{milestoneId}";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MilestoneMapper milestoneMapper;

  @MockBean
  private MilestoneService milestoneServiceMock;

  @Test
  public void testCreateMilestone_validMilestone_returnMilestoneDto() throws Exception {
    // Arrange

    final User creator = createUser();
    final Project project = createProject(creator);
    final Milestone milestone = createMilestone1(project);
    final var createMilestoneDtoRequest = milestoneMapper.toCreateUpdateMilestoneDtoRequest(
        milestone);
    final var expectedMilestoneDto = milestoneMapper.toMilestoneDto(milestone);

    when(milestoneServiceMock.saveMilestone(eq(createMilestoneDtoRequest), eq(project.getId())))
        .thenReturn(expectedMilestoneDto);

    // Act

    final MvcResult mvcResult =
        mockMvc
            .perform(
                post(MILESTONE_API_URL, project.getId())
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonConverter.objectToJson(createMilestoneDtoRequest)))
            .andReturn();

    // Assert

    final var response = mvcResult.getResponse();
    Assertions.assertEquals(200, response.getStatus());

    final var actualMilestoneDto = JsonConverter.jsonToObject(response.getContentAsString(), MilestoneDto.class);
    assertMilestonesAreEqual(expectedMilestoneDto, actualMilestoneDto);
  }

  @Test
  public void testCreateMilestone_invalidMilestoneDates_returnErrorDtoResponse() throws Exception {
    // Arrange

    final var createUpdateMilestoneDtoRequest =
        new CreateUpdateMilestoneDtoRequest(null, null, NOW.plusMonths(1), NOW);

    // Act

    final MvcResult mvcResult =
        mockMvc
            .perform(
                post(MILESTONE_API_URL, 1L)
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonConverter.objectToJson(createUpdateMilestoneDtoRequest)))
            .andReturn();

    // Assert

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
  public void testCreateMilestone_invalidProjectId_returnErrorDtoResponse() throws Exception {
    // Arrange

    final var createUpdateMilestoneDtoRequest =
        new CreateUpdateMilestoneDtoRequest(
            "Basic API", "Basic API blah...", NOW.plusMonths(1), NOW.plusMonths(2));

    // Act

    final MvcResult mvcResult =
        mockMvc
            .perform(
                post(MILESTONE_API_URL, 0L)
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonConverter.objectToJson(createUpdateMilestoneDtoRequest)))
            .andReturn();

    // Assert

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
  public void testUpdateMilestone_validMilestone_returnMilestoneDto() throws Exception {
    // Arrange

    final User creator = createUser();
    final Project project = createProject(creator);
    final Milestone updatedMilestone = createMilestone1(project);
    final var updateMilestoneDtoRequest = milestoneMapper.toCreateUpdateMilestoneDtoRequest(
        updatedMilestone);
    final var expectedMilestoneDto = milestoneMapper.toMilestoneDto(updatedMilestone);

    when(milestoneServiceMock.updateMilestone(
        eq(updateMilestoneDtoRequest), eq(project.getId()), eq(updatedMilestone.getId())))
        .thenReturn(expectedMilestoneDto);

    // Act

    final MvcResult mvcResult =
        mockMvc
            .perform(
                put(SPECIFIC_MILESTONE_API_URL, project.getId(), updatedMilestone.getId())
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonConverter.objectToJson(updateMilestoneDtoRequest)))
            .andReturn();

    // Assert

    final var response = mvcResult.getResponse();
    Assertions.assertEquals(200, response.getStatus());

    final var actualMilestoneDto = JsonConverter.jsonToObject(response.getContentAsString(), MilestoneDto.class);
    assertMilestonesAreEqual(expectedMilestoneDto, actualMilestoneDto);
  }

  @Test
  public void testDeleteMilestone_validMilestone_returnMilestoneDto() throws Exception {
    // Arrange

    final long projectId = 1L;
    final long milestoneId = 1L;

    // no need to set projectService's behavior

    // Act

    final MvcResult mvcResult =
        mockMvc
            .perform(
                delete(SPECIFIC_MILESTONE_API_URL, projectId, milestoneId)
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8))
            .andReturn();

    // Assert

    final var response = mvcResult.getResponse();
    Assertions.assertEquals(200, response.getStatus());
    Assertions.assertEquals(0, response.getContentLength());
  }

  @Test
  public void testGetAllMilestones_validArguments_returnList() throws Exception {
    // Arrange

    final User creator = createUser();
    final Project project = createProject(creator);
    final var milestoneDtos =
        List.of(
            milestoneMapper.toMilestoneDto(createMilestone1(project)),
            milestoneMapper.toMilestoneDto(createMilestone2(project)));

    when(milestoneServiceMock.getAllMilestones(eq(project.getId()))).thenReturn(milestoneDtos);

    // Act

    final MvcResult mvcResult =
        mockMvc
            .perform(
                get(MILESTONE_API_URL, project.getId())
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8))
            .andReturn();

    // Assert

    final var response = mvcResult.getResponse();
    Assertions.assertEquals(200, response.getStatus());

    final var milestonesDto =
        JsonConverter.jsonToListOfObjects(response.getContentAsString(), MilestoneDto.class);
    Assertions.assertEquals(2, milestonesDto.size());
  }
}
