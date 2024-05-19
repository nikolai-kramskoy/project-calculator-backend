package org.example.projectcalculator.controller;

import static org.example.projectcalculator.utility.Asserter.assertProjectsAreEqual;
import static org.example.projectcalculator.utility.Asserter.assertValidationError;
import static org.example.projectcalculator.utility.TestingData.createProject;
import static org.example.projectcalculator.utility.TestingData.createUser;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.nio.charset.StandardCharsets;
import javax.validation.constraints.NotBlank;
import org.example.projectcalculator.controller.utility.JsonConverter;
import org.example.projectcalculator.dto.ProjectDto;
import org.example.projectcalculator.dto.error.ErrorDtoResponse;
import org.example.projectcalculator.dto.request.CreateUpdateProjectDtoRequest;
import org.example.projectcalculator.mapper.ProjectMapper;
import org.example.projectcalculator.service.ProjectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProjectController.class)
@ComponentScan(basePackageClasses = ProjectMapper.class)
@WithMockUser
class ProjectControllerTest {

  private static final String PROJECT_API_URL = "/projects";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ProjectMapper projectMapper;

  @MockBean
  private ProjectService projectServiceMock;

  @Test
  void testCreateProject_validProject_returnProjectDto() throws Exception {
    final var creator = createUser();
    final var project = createProject(creator);
    final var createProjectDtoRequest = projectMapper.toCreateProjectDtoRequest(project);
    final var expectedProjectDto = projectMapper.toProjectDto(project);

    when(projectServiceMock.saveProject(createProjectDtoRequest)).thenReturn(expectedProjectDto);

    final var mvcResult =
        mockMvc
            .perform(
                post(PROJECT_API_URL)
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonConverter.objectToJson(createProjectDtoRequest)))
            .andReturn();

    final var response = mvcResult.getResponse();
    Assertions.assertEquals(200, response.getStatus());

    final var actualProjectDto = JsonConverter.jsonToObject(response.getContentAsString(),
        ProjectDto.class);
    assertProjectsAreEqual(expectedProjectDto, actualProjectDto);
  }

  @Test
  void testCreateProject_invalidProject_returnErrorDtoResponse() throws Exception {
    final var invalidCreateProjectDtoRequest = new CreateUpdateProjectDtoRequest(null, null, null);

    final var mvcResult =
        mockMvc
            .perform(
                post(PROJECT_API_URL)
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonConverter.objectToJson(invalidCreateProjectDtoRequest)))
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
    assertValidationError(errors, "client", notBlankErrorCode, notBlankErrorMessage);
  }
}
