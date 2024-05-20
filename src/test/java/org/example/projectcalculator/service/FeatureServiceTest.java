package org.example.projectcalculator.service;

import static org.example.projectcalculator.Asserter.assertFeaturesAreEqual;
import static org.example.projectcalculator.TestingData.CLOCK;
import static org.example.projectcalculator.TestingData.FEATURE_MAPPER;
import static org.example.projectcalculator.TestingData.createFeature1;
import static org.example.projectcalculator.TestingData.createFeature2;
import static org.example.projectcalculator.TestingData.createMilestone1;
import static org.example.projectcalculator.TestingData.createMilestone2;
import static org.example.projectcalculator.TestingData.createProject;
import static org.example.projectcalculator.TestingData.createUser;
import static org.example.projectcalculator.service.utility.ServiceTestHelper.setSecurityContext;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.example.projectcalculator.model.Feature;
import org.example.projectcalculator.repository.FeatureRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FeatureServiceTest {

  private UserService userServiceMock;
  private ProjectService projectServiceMock;
  private MilestoneService milestoneServiceMock;
  private PriceService priceServiceMock;

  private FeatureRepository featureRepositoryMock;

  private FeatureService featureService;

  @BeforeEach
  public void initMocks() {
    userServiceMock = mock(UserService.class);
    projectServiceMock = mock(ProjectService.class);
    milestoneServiceMock = mock(MilestoneService.class);
    priceServiceMock = mock(PriceService.class);

    featureRepositoryMock = mock(FeatureRepository.class);

    featureService =
        new FeatureService(
            userServiceMock,
            projectServiceMock,
            milestoneServiceMock,
            priceServiceMock,
            featureRepositoryMock,
            CLOCK,
            FEATURE_MAPPER);
  }

  @Test
  void testCreateFeature_validFeatureWithMilestone_returnCreatedFeature() {
    final var creator = createUser();
    final var project = createProject(creator);
    final var milestone = createMilestone1(project);
    final var feature = createFeature1(project, milestone);
    final var createFeatureDtoRequest = FEATURE_MAPPER.toCreateFeatureDtoRequest(feature);
    final var expectedFeatureDto = FEATURE_MAPPER.toFeatureDto(feature);

    when(userServiceMock.getCurrentlyAuthenticatedUser()).thenReturn(creator);
    when(projectServiceMock.getProject(project.getId())).thenReturn(project);
    when(milestoneServiceMock.getMilestone(project.getId(), milestone.getId())).thenReturn(
        milestone);

    when(featureRepositoryMock.save(any(Feature.class))).thenReturn(feature);

    setSecurityContext(creator);

    final var actualFeatureDto = featureService.saveFeature(createFeatureDtoRequest,
        project.getId());

    assertFeaturesAreEqual(expectedFeatureDto, actualFeatureDto);
  }

  @Test
  void testCreateFeature_validFeatureWithoutMilestone_returnCreatedFeature() {
    final var creator = createUser();
    final var project = createProject(creator);
    final var feature = createFeature1(project, null);
    final var createFeatureDtoRequest = FEATURE_MAPPER.toCreateFeatureDtoRequest(feature);
    final var expectedFeatureDto = FEATURE_MAPPER.toFeatureDto(feature);

    when(userServiceMock.getCurrentlyAuthenticatedUser()).thenReturn(creator);
    when(projectServiceMock.getProject(project.getId())).thenReturn(project);

    when(featureRepositoryMock.save(any(Feature.class))).thenReturn(feature);

    setSecurityContext(creator);

    final var actualFeatureDto = featureService.saveFeature(createFeatureDtoRequest,
        project.getId());

    assertFeaturesAreEqual(expectedFeatureDto, actualFeatureDto);
  }

  @Test
  void testUpdateFeature_validFeature_returnUpdatedFeature() {
    final var creator = createUser();
    final var project = createProject(creator);
    final var feature = createFeature1(project, null);
    final var newFeature = createFeature2(project, null);
    newFeature.setId(1L);
    final var updateFeatureDtoRequest = FEATURE_MAPPER.toUpdateFeatureDtoRequest(newFeature, null);
    final var expectedFeatureDto = FEATURE_MAPPER.toFeatureDto(newFeature);

    when(userServiceMock.getCurrentlyAuthenticatedUser()).thenReturn(creator);
    when(projectServiceMock.getProject(project.getId())).thenReturn(project);

    when(featureRepositoryMock.findByIdAndProjectId(feature.getId(), project.getId())).thenReturn(
        Optional.of(feature));

    setSecurityContext(creator);

    final var actualFeatureDto = featureService.updateFeature(updateFeatureDtoRequest,
        project.getId(), feature.getId());

    assertFeaturesAreEqual(expectedFeatureDto, actualFeatureDto);
  }

  @Test
  void testUpdateFeature_validFeatureUpdateMilestone_returnUpdatedFeature() {
    final var creator = createUser();
    final var project = createProject(creator);
    final var oldMilestone = createMilestone1(project);
    final var newMilestone = createMilestone2(project);
    final var feature = createFeature1(project, oldMilestone);
    final var newFeature = createFeature2(project, newMilestone);
    newFeature.setId(1L);
    final var updateFeatureDtoRequest = FEATURE_MAPPER.toUpdateFeatureDtoRequest(newFeature,
        newMilestone.getId());
    final var expectedFeatureDto = FEATURE_MAPPER.toFeatureDto(newFeature);

    when(userServiceMock.getCurrentlyAuthenticatedUser()).thenReturn(creator);
    when(projectServiceMock.getProject(project.getId())).thenReturn(project);
    when(milestoneServiceMock.getMilestone(project.getId(), newMilestone.getId())).thenReturn(
        newMilestone);

    when(featureRepositoryMock.findByIdAndProjectId(feature.getId(), project.getId())).thenReturn(
        Optional.of(feature));

    setSecurityContext(creator);

    final var actualFeatureDto = featureService.updateFeature(updateFeatureDtoRequest,
        project.getId(), feature.getId());

    assertFeaturesAreEqual(expectedFeatureDto, actualFeatureDto);
  }

  @Test
  void testUpdateFeature_validFeatureSetMilestoneToNull_returnUpdatedFeature() {
    final var creator = createUser();
    final var project = createProject(creator);
    final var milestone = createMilestone1(project);
    final var feature = createFeature1(project, milestone);
    final var newFeature = createFeature2(project, null);
    newFeature.setId(1L);
    final var updateFeatureDtoRequest = FEATURE_MAPPER.toUpdateFeatureDtoRequest(newFeature,
        milestone.getId());
    final var expectedFeatureDto = FEATURE_MAPPER.toFeatureDto(newFeature);

    when(userServiceMock.getCurrentlyAuthenticatedUser()).thenReturn(creator);
    when(projectServiceMock.getProject(project.getId())).thenReturn(project);
    when(featureRepositoryMock.findByIdAndProjectId(feature.getId(), project.getId())).thenReturn(
        Optional.of(feature));

    setSecurityContext(creator);

    final var actualFeatureDto = featureService.updateFeature(updateFeatureDtoRequest,
        project.getId(), feature.getId());

    assertFeaturesAreEqual(expectedFeatureDto, actualFeatureDto);
  }

  @Test
  void testGetAllFeatures_validArgumentsWithMilestone_returnList() {
    final var creator = createUser();
    final var project = createProject(creator);
    final var milestone = createMilestone1(project);
    final var feature = createFeature1(project, milestone);

    when(userServiceMock.getCurrentlyAuthenticatedUser()).thenReturn(creator);
    when(projectServiceMock.getProject(project.getId())).thenReturn(project);

    when(featureRepositoryMock.findAllByProjectIdAndMilestoneId(project.getId(),
        milestone.getId())).thenReturn(Collections.singletonList(feature));

    setSecurityContext(creator);

    final var actualFeatureDtos = featureService.getAllFeatures(project.getId(), milestone.getId());

    Assertions.assertNotNull(actualFeatureDtos);
    Assertions.assertEquals(1, actualFeatureDtos.size());
    Assertions.assertEquals(feature.getId(), actualFeatureDtos.get(0).id());
  }

  @Test
  void testGetAllFeatures_validArgumentsWithoutMilestone_returnList() {
    final var creator = createUser();
    final var project = createProject(creator);
    final var feature1 = createFeature1(project, null);
    final var feature2 = createFeature2(project, null);
    final var features = List.of(feature1, feature2);

    when(userServiceMock.getCurrentlyAuthenticatedUser()).thenReturn(creator);
    when(projectServiceMock.getProject(project.getId())).thenReturn(project);

    when(featureRepositoryMock.findAllByProjectId(project.getId())).thenReturn(features);

    setSecurityContext(creator);

    final var actualFeatureDtos = featureService.getAllFeatures(project.getId(), null);

    Assertions.assertNotNull(actualFeatureDtos);
    Assertions.assertEquals(2, actualFeatureDtos.size());
  }
}
