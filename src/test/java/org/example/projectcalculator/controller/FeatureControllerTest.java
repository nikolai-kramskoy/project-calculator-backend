package org.example.projectcalculator.controller;

import static org.example.projectcalculator.Asserter.assertFeaturesAreEqual;
import static org.example.projectcalculator.Asserter.assertValidationError;
import static org.example.projectcalculator.TestingData.FEATURE_MAPPER;
import static org.example.projectcalculator.TestingData.createFeature1;
import static org.example.projectcalculator.TestingData.createFeature2;
import static org.example.projectcalculator.TestingData.createMilestone1;
import static org.example.projectcalculator.TestingData.createMilestone2;
import static org.example.projectcalculator.TestingData.createProject;
import static org.example.projectcalculator.TestingData.createUser;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import org.example.projectcalculator.controller.utility.JsonConverter;
import org.example.projectcalculator.dto.FeatureDto;
import org.example.projectcalculator.dto.error.ErrorDtoResponse;
import org.example.projectcalculator.dto.request.CreateFeatureDtoRequest;
import org.example.projectcalculator.model.Feature;
import org.example.projectcalculator.service.FeatureService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FeatureController.class)
@WithMockUser
class FeatureControllerTest {

  private static final String FEATURES_API_URL = "/projects/{projectId}/features";
  private static final String SPECIFIC_FEATURE_API_URL = FEATURES_API_URL + "/{featureId}";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private FeatureService featureServiceMock;

  @Test
  void testCreateFeatureWithMilestone_validFeature_returnFeatureDto() throws Exception {
    final var creator = createUser();
    final var project = createProject(creator);
    final var milestone = createMilestone1(project);
    final var feature = createFeature1(project, milestone);
    final var createFeatureDtoRequest = FEATURE_MAPPER.toCreateFeatureDtoRequest(feature);
    final var expectedFeatureDto = FEATURE_MAPPER.toFeatureDto(feature);

    when(featureServiceMock.saveFeature(createFeatureDtoRequest, project.getId())).thenReturn(
        expectedFeatureDto);

    final var mvcResult =
        mockMvc
            .perform(
                post(FEATURES_API_URL, project.getId())
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonConverter.objectToJson(createFeatureDtoRequest)))
            .andReturn();

    final var response = mvcResult.getResponse();

    Assertions.assertEquals(200, response.getStatus());

    final var actualFeatureDto = JsonConverter.jsonToObject(response.getContentAsString(),
        FeatureDto.class);

    assertFeaturesAreEqual(expectedFeatureDto, actualFeatureDto);
  }

  @Test
  void testCreateFeature_invalidProjectId_returnErrorDtoResponse() throws Exception {
    final var createFeatureDtoRequest =
        new CreateFeatureDtoRequest(
            "Basic API",
            "Basic API blah...",
            new BigDecimal(2),
            new BigDecimal(4),
            new BigDecimal(8),
            null);

    final var mvcResult =
        mockMvc
            .perform(
                post(FEATURES_API_URL, 0L)
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonConverter.objectToJson(createFeatureDtoRequest)))
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
  void testCreateFeature_invalidEstimates1_returnErrorDtoResponse() throws Exception {
    final var createFeatureDtoRequest =
        new CreateFeatureDtoRequest(
            "Basic API",
            "Basic API blah...",
            // problematic best case estimate
            new BigDecimal(-5),
            new BigDecimal(5),
            new BigDecimal(10),
            null);

    assertFeatureEstimatesValidationFail(createFeatureDtoRequest);
  }

  @Test
  void testCreateFeature_invalidEstimates2_returnErrorDtoResponse() throws Exception {
    final var createFeatureDtoRequest =
        new CreateFeatureDtoRequest(
            "Basic API",
            "Basic API blah...",
            new BigDecimal(2),
            // problematic most likely estimate
            new BigDecimal(1),
            new BigDecimal(5),
            null);

    assertFeatureEstimatesValidationFail(createFeatureDtoRequest);
  }

  @Test
  void testGetAllFeaturesWithMilestoneId_validArguments_returnList() throws Exception {
    final var creator = createUser();
    final var project = createProject(creator);
    final var milestone = createMilestone1(project);
    final var feature1 = createFeature1(project, milestone);
    final var feature2 = createFeature2(project, null);
    final var expectedFeatureDtos = List.of(FEATURE_MAPPER.toFeatureDto(feature1),
        FEATURE_MAPPER.toFeatureDto(feature2));

    when(featureServiceMock.getAllFeatures(project.getId(), milestone.getId())).thenReturn(
        Collections.singletonList(expectedFeatureDtos.get(0)));

    final var mvcResult =
        mockMvc
            .perform(
                get(FEATURES_API_URL, project.getId())
                    .param("milestoneId", String.valueOf(milestone.getId()))
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8))
            .andReturn();

    final var response = mvcResult.getResponse();

    Assertions.assertEquals(200, response.getStatus());

    final var actualFeatureDtos = JsonConverter.jsonToListOfObjects(response.getContentAsString(),
        FeatureDto.class);

    Assertions.assertEquals(1, actualFeatureDtos.size());
  }

  @Test
  void testGetAllFeaturesWithoutMilestoneId_validArguments_returnList() throws Exception {
    final var creator = createUser();
    final var project = createProject(creator);
    final var feature1 = createFeature1(project, null);
    final var feature2 = createFeature2(project, null);
    final var expectedFeatureDtos = List.of(FEATURE_MAPPER.toFeatureDto(feature1),
        FEATURE_MAPPER.toFeatureDto(feature2));

    when(featureServiceMock.getAllFeatures(project.getId(), null)).thenReturn(expectedFeatureDtos);

    final var mvcResult =
        mockMvc
            .perform(
                get(FEATURES_API_URL, project.getId())
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8))
            .andReturn();

    final var response = mvcResult.getResponse();

    Assertions.assertEquals(200, response.getStatus());

    final var actualFeatureDtos = JsonConverter.jsonToListOfObjects(response.getContentAsString(),
        FeatureDto.class);

    Assertions.assertEquals(2, actualFeatureDtos.size());
  }

  @Test
  void testUpdateFeature_validFeature_returnFeatureDto() throws Exception {
    final var creator = createUser();
    final var project = createProject(creator);
    final var feature = createFeature1(project, null);
    final var updatedFeature = createUpdatedFeature(feature);
    final var updateFeatureDtoRequest = FEATURE_MAPPER.toUpdateFeatureDtoRequest(updatedFeature,
        null);
    final var expectedFeatureDto = FEATURE_MAPPER.toFeatureDto(updatedFeature);

    when(featureServiceMock.updateFeature(updateFeatureDtoRequest, project.getId(),
        feature.getId())).thenReturn(expectedFeatureDto);

    final var mvcResult =
        mockMvc
            .perform(
                put(SPECIFIC_FEATURE_API_URL, project.getId(), feature.getId())
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonConverter.objectToJson(updateFeatureDtoRequest)))
            .andReturn();

    final var response = mvcResult.getResponse();

    Assertions.assertEquals(200, response.getStatus());

    final var actualFeatureDto = JsonConverter.jsonToObject(response.getContentAsString(),
        FeatureDto.class);

    assertFeaturesAreEqual(expectedFeatureDto, actualFeatureDto);
  }

  @Test
  void testUpdateFeatureUpdateMilestone_validFeature_returnFeatureDto() throws Exception {
    final var creator = createUser();
    final var project = createProject(creator);
    final var oldMilestone = createMilestone1(project);
    final var newMilestone = createMilestone2(project);
    final var feature = createFeature1(project, oldMilestone);
    final var updatedFeature = createUpdatedFeature(feature);
    final var updateFeatureDtoRequest = FEATURE_MAPPER.toUpdateFeatureDtoRequest(updatedFeature,
        newMilestone.getId());
    final var expectedFeatureDto = FEATURE_MAPPER.toFeatureDto(updatedFeature);

    when(featureServiceMock.updateFeature(updateFeatureDtoRequest, project.getId(),
        feature.getId())).thenReturn(expectedFeatureDto);

    final var mvcResult =
        mockMvc
            .perform(
                put(SPECIFIC_FEATURE_API_URL, project.getId(), feature.getId())
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonConverter.objectToJson(updateFeatureDtoRequest)))
            .andReturn();

    final var response = mvcResult.getResponse();

    Assertions.assertEquals(200, response.getStatus());

    final var actualFeatureDto = JsonConverter.jsonToObject(response.getContentAsString(),
        FeatureDto.class);

    assertFeaturesAreEqual(expectedFeatureDto, actualFeatureDto);
  }

  private void assertFeatureEstimatesValidationFail(
      final CreateFeatureDtoRequest createFeatureDtoRequest) throws Exception {
    final var mvcResult =
        mockMvc
            .perform(
                post(FEATURES_API_URL, 1L)
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonConverter.objectToJson(createFeatureDtoRequest)))
            .andReturn();

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
