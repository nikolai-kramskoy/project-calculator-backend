package org.example.projectcalculator.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum ProjectCalculatorError {
  LOGIN_ALREADY_EXISTS("User with specified login already exists"),
  WRONG_LOGIN_OR_PASSWORD("User with specified login and password does not exist"),
  USER_IS_NOT_FOUND_BY_ID("User with specified id is not found"),
  PROJECT_IS_NOT_FOUND_BY_ID("Project with specified id is not found"),
  RATE_IS_NOT_FOUND_BY_ID("Rate with specified id is not found"),
  TEAM_MEMBER_IS_NOT_FOUND_BY_ID("Team member with specified id is not found"),
  TEAM_MEMBER_ALREADY_EXISTS("Team member with this position already exists"),
  WRONG_POSITION("Such position does not exist"),
  MILESTONE_IS_NOT_FOUND_BY_ID("Milestone with specified id is not found"),
  FEATURE_IS_NOT_FOUND_BY_ID("Feature with specified id is not found"),
  ;

  private final String message;
}
