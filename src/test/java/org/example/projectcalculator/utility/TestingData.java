package org.example.projectcalculator.utility;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.example.projectcalculator.model.Feature;
import org.example.projectcalculator.model.Milestone;
import org.example.projectcalculator.model.Position;
import org.example.projectcalculator.model.Project;
import org.example.projectcalculator.model.TeamMember;
import org.example.projectcalculator.model.User;

public class TestingData {

  public static final Clock CLOCK = Clock.systemUTC();

  public static final LocalDateTime NOW = LocalDateTime.now(CLOCK);

  private TestingData() {
  }

  public static User createUser() {
    return new User(1L, "someUser", "{noop}qwerty123", "blah@example.com", NOW, NOW);
  }

  public static Project createProject(final User creator) {
    return new Project(
        1L,
        "Weather Telegram bot",
        "Telegram bot that provides current weather in your location",
        "Anonym",
        creator,
        BigDecimal.ZERO,
        new ArrayList<>(),
        new ArrayList<>(),
        NOW,
        NOW);
  }

  public static Milestone createMilestone1(final Project project) {
    return new Milestone(
        1L,
        project,
        "Basic API",
        "Basic API blah...",
        NOW.plusWeeks(2),
        NOW.plusMonths(3),
        BigDecimal.ZERO,
        NOW,
        NOW);
  }

  public static Milestone createMilestone2(final Project project) {
    return new Milestone(
        2L,
        project,
        "Advanced API",
        "Advanced API blah...",
        NOW.plusMonths(1),
        NOW.plusYears(1),
        BigDecimal.ZERO,
        NOW,
        NOW);
  }

  public static Feature createFeature1(final Project project, final Milestone milestone) {
    return new Feature(
        1L,
        project,
        milestone,
        "Implement register user",
        "Implement register user blah...",
        new BigDecimal(2),
        new BigDecimal(4),
        new BigDecimal(8),
        NOW,
        NOW);
  }

  public static Feature createFeature2(final Project project, final Milestone milestone) {
    return new Feature(
        2L,
        project,
        milestone,
        "Implement login user",
        "Implement login user blah...",
        new BigDecimal(1),
        new BigDecimal(2),
        new BigDecimal(3),
        NOW,
        NOW);
  }

  public static TeamMember createTeamMember(final Project project) {
    return new TeamMember(
        23L,
        Position.SENIOR_DEVELOPER,
        new BigDecimal("2.25"),
        project);
  }
}
