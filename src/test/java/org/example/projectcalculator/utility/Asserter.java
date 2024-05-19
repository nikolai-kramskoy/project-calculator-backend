package org.example.projectcalculator.utility;

import java.util.List;
import org.example.projectcalculator.dto.FeatureDto;
import org.example.projectcalculator.dto.MilestoneDto;
import org.example.projectcalculator.dto.ProjectDto;
import org.example.projectcalculator.dto.UserDto;
import org.example.projectcalculator.dto.error.ErrorDto;
import org.junit.jupiter.api.Assertions;

public class Asserter {

  private Asserter() {
  }

  public static void assertValidationError(
      final List<ErrorDto> errors,
      final String expectedFieldWithError,
      final String expectedErrorCode,
      final String expectedErrorMessage) {
    final var error =
        errors.stream()
            .filter(errorDto -> errorDto.fieldWithError().equals(expectedFieldWithError))
            .findAny();

    Assertions.assertTrue(error.isPresent());
    Assertions.assertEquals(expectedErrorCode, error.get().errorCode());
    Assertions.assertEquals(expectedErrorMessage, error.get().errorMessage());
  }

  public static void assertUsersAreEqual(
      final UserDto expectedUser, final UserDto actualUser) {
    Assertions.assertEquals(expectedUser.id(), actualUser.id());
    Assertions.assertEquals(expectedUser.login(), actualUser.login());
    Assertions.assertEquals(expectedUser.email(), actualUser.email());
  }

  public static void assertProjectsAreEqual(
      final ProjectDto expectedProject, final ProjectDto actualProject) {
    Assertions.assertEquals(expectedProject.id(), actualProject.id());
    Assertions.assertEquals(expectedProject.title(), actualProject.title());
    Assertions.assertEquals(expectedProject.description(), actualProject.description());
    Assertions.assertEquals(expectedProject.client(), actualProject.client());
    Assertions.assertEquals(expectedProject.creatorId(), actualProject.creatorId());
    Assertions.assertEquals(expectedProject.estimateInDays(), actualProject.estimateInDays());
    Assertions.assertEquals(expectedProject.priceInRubles(), actualProject.priceInRubles());
  }

  public static void assertMilestonesAreEqual(
      final MilestoneDto expectedMilestone, final MilestoneDto actualMilestone) {
    Assertions.assertEquals(expectedMilestone.id(), actualMilestone.id());
    Assertions.assertEquals(expectedMilestone.projectId(), actualMilestone.projectId());
    Assertions.assertEquals(expectedMilestone.title(), actualMilestone.title());
    Assertions.assertEquals(expectedMilestone.description(), actualMilestone.description());
    Assertions.assertEquals(expectedMilestone.startDateTime(), actualMilestone.startDateTime());
    Assertions.assertEquals(expectedMilestone.endDateTime(), actualMilestone.endDateTime());
    Assertions.assertEquals(expectedMilestone.estimateInDays(), actualMilestone.estimateInDays());
    Assertions.assertEquals(expectedMilestone.priceInRubles(), actualMilestone.priceInRubles());
  }

  public static void assertFeaturesAreEqual(
      final FeatureDto expectedFeature, final FeatureDto actualFeature) {
    Assertions.assertEquals(expectedFeature.id(), actualFeature.id());
    Assertions.assertEquals(expectedFeature.projectId(), actualFeature.projectId());
    Assertions.assertEquals(expectedFeature.milestoneId(), actualFeature.milestoneId());
    Assertions.assertEquals(expectedFeature.title(), actualFeature.title());
    Assertions.assertEquals(expectedFeature.description(), actualFeature.description());
    Assertions.assertEquals(expectedFeature.bestCaseEstimateInDays(),
        actualFeature.bestCaseEstimateInDays());
    Assertions.assertEquals(expectedFeature.mostLikelyEstimateInDays(),
        actualFeature.mostLikelyEstimateInDays());
    Assertions.assertEquals(expectedFeature.worstCaseEstimateInDays(),
        actualFeature.worstCaseEstimateInDays());
    Assertions.assertEquals(expectedFeature.estimateInDays(),
        actualFeature.estimateInDays());
  }
}
