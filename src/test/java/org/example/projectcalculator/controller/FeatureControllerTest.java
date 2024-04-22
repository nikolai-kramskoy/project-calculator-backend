package org.example.projectcalculator.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.example.projectcalculator.utility.Asserter.assertFeaturesAreEqual;
import static org.example.projectcalculator.utility.Asserter.assertValidationError;
import static org.example.projectcalculator.utility.TestingData.createFeature1;
import static org.example.projectcalculator.utility.TestingData.createFeature2;
import static org.example.projectcalculator.utility.TestingData.createMilestone1;
import static org.example.projectcalculator.utility.TestingData.createMilestone2;
import static org.example.projectcalculator.utility.TestingData.createProject;
import static org.example.projectcalculator.utility.TestingData.createUser;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
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
import org.example.projectcalculator.dto.FeatureDto;
import org.example.projectcalculator.dto.error.ErrorDtoResponse;
import org.example.projectcalculator.dto.request.CreateFeatureDtoRequest;
import org.example.projectcalculator.mapper.FeatureMapper;
import org.example.projectcalculator.model.Feature;
import org.example.projectcalculator.model.Milestone;
import org.example.projectcalculator.model.Project;
import org.example.projectcalculator.model.User;
import org.example.projectcalculator.service.FeatureService;

@WebMvcTest(FeatureController.class)
@ComponentScan(basePackageClasses = FeatureMapper.class)
@WithMockUser
public class FeatureControllerTest {

  private static final String FEATURE_API_URL = "/projects/{projectId}/features";
  private static final String SPECIFIC_FEATURE_API_URL = FEATURE_API_URL + "/{featureId}";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private FeatureMapper featureMapper;

  @MockBean
  private FeatureService featureServiceMock;

  @Test
  public void testCreateFeatureWithMilestone_validFeature_returnFeatureDto() throws Exception {
    // Arrange

    final User creator = createUser();
    final Project project = createProject(creator);
    final Milestone milestone = createMilestone1(project);
    final Feature feature = createFeature1(project, milestone);
    final var createFeatureDtoRequest = featureMapper.toCreateFeatureDtoRequest(feature);
    final var expectedFeatureDto = featureMapper.toFeatureDto(feature);

    when(featureServiceMock.saveFeature(eq(createFeatureDtoRequest), eq(project.getId())))
        .thenReturn(expectedFeatureDto);

    // Act

    final MvcResult mvcResult =
        mockMvc
            .perform(
                post(FEATURE_API_URL, project.getId())
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonConverter.objectToJson(createFeatureDtoRequest)))
            .andReturn();

    // Assert

    final var response = mvcResult.getResponse();
    Assertions.assertEquals(200, response.getStatus());

    final var actualFeatureDto = JsonConverter.jsonToObject(response.getContentAsString(), FeatureDto.class);
    assertFeaturesAreEqual(expectedFeatureDto, actualFeatureDto);
  }

  @Test
  public void testCreateFeature_invalidProjectId_returnErrorDtoResponse() throws Exception {
    // Arrange

    final var createFeatureDtoRequest =
        new CreateFeatureDtoRequest(
            "Basic API",
            "Basic API blah...",
            new BigDecimal(2),
            new BigDecimal(4),
            new BigDecimal(8),
            null);

    // Act

    final MvcResult mvcResult =
        mockMvc
            .perform(
                post(FEATURE_API_URL, 0L)
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonConverter.objectToJson(createFeatureDtoRequest)))
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
  public void testCreateFeature_invalidEstimates1_returnErrorDtoResponse() throws Exception {
    // Arrange

    final var createFeatureDtoRequest =
        new CreateFeatureDtoRequest(
            "Basic API",
            "Basic API blah...",
            // problematic best case estimate
            new BigDecimal(-5),
            new BigDecimal(5),
            new BigDecimal(10),
            null);

    // Act, assert

    assertFeatureEstimatesValidationFail(createFeatureDtoRequest);
  }

  @Test
  public void testCreateFeature_invalidEstimates2_returnErrorDtoResponse() throws Exception {
    // Arrange

    final var createFeatureDtoRequest =
        new CreateFeatureDtoRequest(
            "Basic API",
            "Basic API blah...",
            new BigDecimal(2),
            // problematic most likely estimate
            new BigDecimal(1),
            new BigDecimal(5),
            null);

    // Act, assert

    assertFeatureEstimatesValidationFail(createFeatureDtoRequest);
  }

  @Test
  public void testUpdateFeature_validFeature_returnFeatureDto() throws Exception {
    // Arrange

    final User creator = createUser();
    final Project project = createProject(creator);
    final Feature feature = createFeature1(project, null);
    final Feature updatedFeature = createUpdatedFeature(feature);
    final var updateFeatureDtoRequest = featureMapper.toUpdateFeatureDtoRequest(updatedFeature,
        null);
    final var expectedFeatureDto = featureMapper.toFeatureDto(updatedFeature);

    when(featureServiceMock.updateFeature(
        eq(updateFeatureDtoRequest), eq(project.getId()), eq(feature.getId())))
        .thenReturn(expectedFeatureDto);

    // Act

    final MvcResult mvcResult =
        mockMvc
            .perform(
                put(SPECIFIC_FEATURE_API_URL, project.getId(), feature.getId())
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonConverter.objectToJson(updateFeatureDtoRequest)))
            .andReturn();

    // Assert

    final var response = mvcResult.getResponse();
    Assertions.assertEquals(200, response.getStatus());

    final var actualFeatureDto = JsonConverter.jsonToObject(response.getContentAsString(), FeatureDto.class);
    assertFeaturesAreEqual(expectedFeatureDto, actualFeatureDto);
  }

  @Test
  public void testUpdateFeatureUpdateMilestone_validFeature_returnFeatureDto() throws Exception {
    // Arrange

    final User creator = createUser();
    final Project project = createProject(creator);
    final Milestone oldMilestone = createMilestone1(project);
    final Milestone newMilestone = createMilestone2(project);
    final Feature feature = createFeature1(project, oldMilestone);
    final Feature updatedFeature = createUpdatedFeature(feature);
    final var updateFeatureDtoRequest = featureMapper.toUpdateFeatureDtoRequest(updatedFeature,
        newMilestone.getId());
    final var expectedFeatureDto = featureMapper.toFeatureDto(updatedFeature);

    when(featureServiceMock.updateFeature(
        eq(updateFeatureDtoRequest), eq(project.getId()), eq(feature.getId())))
        .thenReturn(expectedFeatureDto);

    // Act

    final MvcResult mvcResult =
        mockMvc
            .perform(
                put(SPECIFIC_FEATURE_API_URL, project.getId(), feature.getId())
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonConverter.objectToJson(updateFeatureDtoRequest)))
            .andReturn();

    // Assert

    final var response = mvcResult.getResponse();
    Assertions.assertEquals(200, response.getStatus());

    final var actualFeatureDto = JsonConverter.jsonToObject(response.getContentAsString(), FeatureDto.class);
    assertFeaturesAreEqual(expectedFeatureDto, actualFeatureDto);
  }

  @Test
  public void testGetAllFeaturesWithMilestoneId_validArguments_returnList() throws Exception {
    // Arrange

    final User creator = createUser();
    final Project project = createProject(creator);
    final Milestone milestone = createMilestone1(project);
    final Feature feature1 = createFeature1(project, milestone);
    final Feature feature2 = createFeature2(project, null);
    final var expectedFeatureDtos = List.of(featureMapper.toFeatureDto(feature1),
        featureMapper.toFeatureDto(feature2));

    when(featureServiceMock.getAllFeatures(eq(project.getId()), eq(milestone.getId())))
        .thenReturn(Collections.singletonList(expectedFeatureDtos.get(0)));

    // Act

    final MvcResult mvcResult =
        mockMvc
            .perform(
                get(FEATURE_API_URL, project.getId())
                    .param("milestoneId", String.valueOf(milestone.getId()))
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8))
            .andReturn();

    // Assert

    final var response = mvcResult.getResponse();
    Assertions.assertEquals(200, response.getStatus());

    final var actualFeatureDtos = JsonConverter.jsonToListOfObjects(response.getContentAsString(),
        FeatureDto.class);
    Assertions.assertEquals(1, actualFeatureDtos.size());
  }

  @Test
  public void testGetAllFeaturesWithoutMilestoneId_validArguments_returnList() throws Exception {
    // Arrange

    final User creator = createUser();
    final Project project = createProject(creator);
    final Feature feature1 = createFeature1(project, null);
    final Feature feature2 = createFeature2(project, null);
    final var expectedFeatureDtos = List.of(featureMapper.toFeatureDto(feature1),
        featureMapper.toFeatureDto(feature2));

    when(featureServiceMock.getAllFeatures(eq(project.getId()), eq(null)))
        .thenReturn(expectedFeatureDtos);

    // Act

    final MvcResult mvcResult =
        mockMvc
            .perform(
                get(FEATURE_API_URL, project.getId())
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8))
            .andReturn();

    // Assert

    final var response = mvcResult.getResponse();
    Assertions.assertEquals(200, response.getStatus());

    final var actualFeatureDtos = JsonConverter.jsonToListOfObjects(response.getContentAsString(),
        FeatureDto.class);
    Assertions.assertEquals(2, actualFeatureDtos.size());
  }

  private void assertFeatureEstimatesValidationFail(
      final CreateFeatureDtoRequest createFeatureDtoRequest) throws Exception {
    // Act

    final MvcResult mvcResult =
        mockMvc
            .perform(
                post(FEATURE_API_URL, 1L)
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonConverter.objectToJson(createFeatureDtoRequest)))
            .andReturn();

    // Assert

    final var response = mvcResult.getResponse();
    Assertions.assertEquals(400, response.getStatus());

    final var errorDtoResponse =
        JsonConverter.jsonToObject(response.getContentAsString(), ErrorDtoResponse.class);
    // Must be 1 validation error
    Assertions.assertEquals(1, errorDtoResponse.errors().size());

    final var errors = errorDtoResponse.errors();
    assertValidationError(
        errors,
        "createFeatureDtoRequest",
        "FeatureEstimates",
        "all feature estimates must be > 0.0 and a <= m <= b");
  }

  private Feature createUpdatedFeature(final Feature feature) {
    return new Feature(
        feature.getId(),
        feature.getProject(),
        feature.getMilestone(),
        "blah title",
        "blah description",
        new BigDecimal(5),
        new BigDecimal(7),
        new BigDecimal(8),
        feature.getCreatedAt(),
        feature.getLastUpdatedAt().plusHours(5));
  }
}
