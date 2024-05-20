package org.example.projectcalculator;

import java.util.List;
import org.example.projectcalculator.dto.FeatureDto;
import org.example.projectcalculator.dto.MilestoneDto;
import org.example.projectcalculator.dto.ProjectDto;
import org.example.projectcalculator.dto.RateDto;
import org.example.projectcalculator.dto.TeamMemberDto;
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
      final UserDto expectedUserDto, final UserDto actualUserDto) {
    Assertions.assertEquals(expectedUserDto.id(), actualUserDto.id());
    Assertions.assertEquals(expectedUserDto.login(), actualUserDto.login());
    Assertions.assertEquals(expectedUserDto.email(), actualUserDto.email());
  }

  public static void assertProjectsAreEqual(
      final ProjectDto expectedProjectDto, final ProjectDto actualProjectDto) {
    Assertions.assertEquals(expectedProjectDto.id(), actualProjectDto.id());
    Assertions.assertEquals(expectedProjectDto.title(), actualProjectDto.title());
    Assertions.assertEquals(expectedProjectDto.description(), actualProjectDto.description());
    Assertions.assertEquals(expectedProjectDto.client(), actualProjectDto.client());
    Assertions.assertEquals(expectedProjectDto.creatorId(), actualProjectDto.creatorId());
    Assertions.assertEquals(expectedProjectDto.estimateInDays(), actualProjectDto.estimateInDays());
    Assertions.assertEquals(expectedProjectDto.priceInRubles(), actualProjectDto.priceInRubles());
  }

  public static void assertMilestonesAreEqual(
      final MilestoneDto expectedMilestoneDto, final MilestoneDto actualMilestoneDto) {
    Assertions.assertEquals(expectedMilestoneDto.id(), actualMilestoneDto.id());
    Assertions.assertEquals(expectedMilestoneDto.projectId(), actualMilestoneDto.projectId());
    Assertions.assertEquals(expectedMilestoneDto.title(), actualMilestoneDto.title());
    Assertions.assertEquals(expectedMilestoneDto.description(), actualMilestoneDto.description());
    Assertions.assertEquals(expectedMilestoneDto.startDateTime(),
        actualMilestoneDto.startDateTime());
    Assertions.assertEquals(expectedMilestoneDto.endDateTime(), actualMilestoneDto.endDateTime());
    Assertions.assertEquals(expectedMilestoneDto.estimateInDays(),
        actualMilestoneDto.estimateInDays());
    Assertions.assertEquals(expectedMilestoneDto.priceInRubles(),
        actualMilestoneDto.priceInRubles());
  }

  public static void assertFeaturesAreEqual(
      final FeatureDto expectedFeatureDto, final FeatureDto actualFeatureDto) {
    Assertions.assertEquals(expectedFeatureDto.id(), actualFeatureDto.id());
    Assertions.assertEquals(expectedFeatureDto.projectId(), actualFeatureDto.projectId());
    Assertions.assertEquals(expectedFeatureDto.milestoneId(), actualFeatureDto.milestoneId());
    Assertions.assertEquals(expectedFeatureDto.title(), actualFeatureDto.title());
    Assertions.assertEquals(expectedFeatureDto.description(), actualFeatureDto.description());
    Assertions.assertEquals(expectedFeatureDto.bestCaseEstimateInDays(),
        actualFeatureDto.bestCaseEstimateInDays());
    Assertions.assertEquals(expectedFeatureDto.mostLikelyEstimateInDays(),
        actualFeatureDto.mostLikelyEstimateInDays());
    Assertions.assertEquals(expectedFeatureDto.worstCaseEstimateInDays(),
        actualFeatureDto.worstCaseEstimateInDays());
    Assertions.assertEquals(expectedFeatureDto.estimateInDays(), actualFeatureDto.estimateInDays());
  }

  public static void assertTeamMembersAreEqual(final TeamMemberDto expectedTeamMemberDto,
      final TeamMemberDto actualTeamMemberDto) {
    Assertions.assertEquals(expectedTeamMemberDto.id(), actualTeamMemberDto.id());
    Assertions.assertEquals(expectedTeamMemberDto.position(), actualTeamMemberDto.position());
    Assertions.assertEquals(expectedTeamMemberDto.numberOfTeamMembers(),
        actualTeamMemberDto.numberOfTeamMembers());
  }

  public static void assertRatesAreEqual(final RateDto expectedRateDto,
      final RateDto actualRateDto) {
    Assertions.assertEquals(expectedRateDto.id(), actualRateDto.id());
    Assertions.assertEquals(expectedRateDto.position(), actualRateDto.position());
    Assertions.assertEquals(expectedRateDto.rublesPerHour(), actualRateDto.rublesPerHour());
  }
}
