package org.example.projectcalculator.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.example.projectcalculator.service.utility.ServiceTestHelper.setSecurityContext;
import static org.example.projectcalculator.utility.Asserter.assertFeaturesAreEqual;
import static org.example.projectcalculator.utility.TestingData.CLOCK;
import static org.example.projectcalculator.utility.TestingData.createFeature1;
import static org.example.projectcalculator.utility.TestingData.createFeature2;
import static org.example.projectcalculator.utility.TestingData.createMilestone1;
import static org.example.projectcalculator.utility.TestingData.createMilestone2;
import static org.example.projectcalculator.utility.TestingData.createProject;
import static org.example.projectcalculator.utility.TestingData.createUser;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.example.projectcalculator.dto.FeatureDto;
import org.example.projectcalculator.mapper.FeatureMapper;
import org.example.projectcalculator.model.Feature;
import org.example.projectcalculator.model.Milestone;
import org.example.projectcalculator.model.Project;
import org.example.projectcalculator.model.User;
import org.example.projectcalculator.repository.FeatureRepository;
import org.example.projectcalculator.repository.RateRepository;
import org.example.projectcalculator.repository.TeamMemberRepository;

public class FeatureServiceTest {

  private UserService userServiceMock;
  private ProjectService projectServiceMock;
  private MilestoneService milestoneServiceMock;

  private FeatureRepository featureRepositoryMock;
  private RateRepository rateRepositoryMock;
  private TeamMemberRepository teamMemberRepositoryMock;

  private static final FeatureMapper FEATURE_MAPPER = Mappers.getMapper(FeatureMapper.class);

  private FeatureService featureService;

  @BeforeEach
  public void initMocks() {
    userServiceMock = mock(UserService.class);
    projectServiceMock = mock(ProjectService.class);
    milestoneServiceMock = mock(MilestoneService.class);

    featureRepositoryMock = mock(FeatureRepository.class);
    rateRepositoryMock = mock(RateRepository.class);
    teamMemberRepositoryMock = mock(TeamMemberRepository.class);

    featureService =
        new FeatureService(
            userServiceMock,
            projectServiceMock,
            milestoneServiceMock,
            featureRepositoryMock,
            rateRepositoryMock,
            teamMemberRepositoryMock,
            CLOCK,
            FEATURE_MAPPER);
  }

  @Test
  public void testCreateFeatureWithMilestone_validFeature_returnCreatedFeature() {
    // Arrange

    final User creator = createUser();
    final Project project = createProject(creator);
    final Milestone milestone = createMilestone1(project);
    final Feature feature = createFeature1(project, milestone);
    final var createFeatureDtoRequest = FEATURE_MAPPER.toCreateFeatureDtoRequest(feature);
    final var expectedFeatureDto = FEATURE_MAPPER.toFeatureDto(feature);

    setSecurityContext(creator);

    when(projectServiceMock.getProject(eq(project.getId()))).thenReturn(project);
    when(milestoneServiceMock.getMilestone(eq(project.getId()), eq(milestone.getId())))
        .thenReturn(milestone);

    when(featureRepositoryMock.save(any(Feature.class))).thenReturn(feature);

    // Act

    final FeatureDto actualFeatureDto = featureService.saveFeature(createFeatureDtoRequest,
        project.getId());

    // Assert

    assertFeaturesAreEqual(expectedFeatureDto, actualFeatureDto);
  }

  @Test
  public void testCreateFeatureWithoutMilestone_validFeature_returnCreatedFeature() {
    // Arrange

    final User creator = createUser();
    final Project project = createProject(creator);
    final Feature feature = createFeature1(project, null);
    final var createFeatureDtoRequest = FEATURE_MAPPER.toCreateFeatureDtoRequest(feature);
    final var expectedFeatureDto = FEATURE_MAPPER.toFeatureDto(feature);

    setSecurityContext(creator);

    when(projectServiceMock.getProject(eq(project.getId()))).thenReturn(project);

    when(featureRepositoryMock.save(any(Feature.class))).thenReturn(feature);

    // Act

    final FeatureDto actualFeatureDto = featureService.saveFeature(createFeatureDtoRequest,
        project.getId());

    // Assert

    assertFeaturesAreEqual(expectedFeatureDto, actualFeatureDto);
  }

  @Test
  public void testUpdateFeature_validFeature_returnUpdatedFeature() {
    // Arrange

    final User creator = createUser();
    final Project project = createProject(creator);
    final Feature feature = createFeature1(project, null);
    final Feature newFeature = createFeature2(project, null);
    newFeature.setId(1L);
    final var updateFeatureDtoRequest = FEATURE_MAPPER.toUpdateFeatureDtoRequest(newFeature, null);
    final var expectedFeatureDto = FEATURE_MAPPER.toFeatureDto(newFeature);

    setSecurityContext(creator);

    when(featureRepositoryMock.findByIdAndProjectId(eq(feature.getId()), eq(project.getId())))
        .thenReturn(Optional.of(feature));

    // Act

    final FeatureDto actualFeatureDto = featureService.updateFeature(updateFeatureDtoRequest,
        project.getId(), feature.getId());

    // Assert

    assertFeaturesAreEqual(expectedFeatureDto, actualFeatureDto);
  }

  @Test
  public void testUpdateFeatureUpdateMilestone_validFeature_returnUpdatedFeature() {
    // Arrange

    final User creator = createUser();
    final Project project = createProject(creator);
    final Milestone oldMilestone = createMilestone1(project);
    final Milestone newMilestone = createMilestone2(project);
    final Feature feature = createFeature1(project, oldMilestone);
    final Feature newFeature = createFeature2(project, newMilestone);
    newFeature.setId(1L);
    final var updateFeatureDtoRequest = FEATURE_MAPPER.toUpdateFeatureDtoRequest(newFeature,
        newMilestone.getId());
    final var expectedFeatureDto = FEATURE_MAPPER.toFeatureDto(newFeature);

    setSecurityContext(creator);

    when(milestoneServiceMock.getMilestone(eq(project.getId()), eq(newMilestone.getId())))
        .thenReturn(newMilestone);
    when(featureRepositoryMock.findByIdAndProjectId(eq(feature.getId()), eq(project.getId())))
        .thenReturn(Optional.of(feature));

    // Act

    final FeatureDto actualFeatureDto = featureService.updateFeature(updateFeatureDtoRequest,
        project.getId(), feature.getId());

    // Assert

    assertFeaturesAreEqual(expectedFeatureDto, actualFeatureDto);
  }

  @Test
  public void testUpdateFeatureSetMilestoneToNull_validFeature_returnUpdatedFeature() {
    // Arrange

    final User creator = createUser();
    final Project project = createProject(creator);
    final Milestone milestone = createMilestone1(project);
    final Feature feature = createFeature1(project, milestone);
    final Feature newFeature = createFeature2(project, null);
    newFeature.setId(1L);
    final var updateFeatureDtoRequest = FEATURE_MAPPER.toUpdateFeatureDtoRequest(newFeature,
        milestone.getId());
    final var expectedFeatureDto = FEATURE_MAPPER.toFeatureDto(newFeature);

    setSecurityContext(creator);

    when(featureRepositoryMock.findByIdAndProjectId(eq(feature.getId()), eq(project.getId())))
        .thenReturn(Optional.of(feature));

    // Act

    final FeatureDto actualFeatureDto = featureService.updateFeature(updateFeatureDtoRequest,
        project.getId(), feature.getId());

    // Assert

    assertFeaturesAreEqual(expectedFeatureDto, actualFeatureDto);
  }

  @Test
  public void testGetAllFeaturesWithMilestone_validArguments_returnList() {
    // Arrange

    final User creator = createUser();
    final Project project = createProject(creator);
    final Milestone milestone = createMilestone1(project);
    final Feature feature1 = createFeature1(project, milestone);
    final Feature feature2 = createFeature2(project, null);
    final var features = List.of(feature1, feature2);

    setSecurityContext(creator);

    when(featureRepositoryMock.findAllByProjectIdAndMilestoneId(
        eq(project.getId()), eq(milestone.getId())))
        .thenReturn(Collections.singletonList(features.get(0)));

    // Act

    final var actualFeatureDtos =
        featureService.getAllFeatures(project.getId(), milestone.getId());

    // Assert

    Assertions.assertNotNull(actualFeatureDtos);
    Assertions.assertEquals(1, actualFeatureDtos.size());
  }

  @Test
  public void testGetAllFeaturesWithoutMilestone_validArguments_returnList() {
    // Arrange

    final User creator = createUser();
    final Project project = createProject(creator);
    final Feature feature1 = createFeature1(project, null);
    final Feature feature2 = createFeature2(project, null);
    final var features = List.of(feature1, feature2);

    setSecurityContext(creator);

    when(featureRepositoryMock.findAllByProjectId(eq(project.getId()))).thenReturn(features);

    // Act

    final var actualFeatureDtos = featureService.getAllFeatures(project.getId(), null);

    // Assert

    Assertions.assertNotNull(actualFeatureDtos);
    Assertions.assertEquals(2, actualFeatureDtos.size());
  }
}
