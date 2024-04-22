package org.example.projectcalculator.error;

import lombok.Getter;

@Getter
public class ProjectCalculatorException extends RuntimeException {

  private final ProjectCalculatorError projectCalculatorError;
  private final String fieldWithError;

  public ProjectCalculatorException(final ProjectCalculatorError projectCalculatorError) {
    super(projectCalculatorError.getMessage());

    this.projectCalculatorError = projectCalculatorError;
    this.fieldWithError = null;
  }

  public ProjectCalculatorException(
      final ProjectCalculatorError projectCalculatorError, final String fieldWithError) {
    super(
        String.format(
            "error message: %s, field with error: %s",
            projectCalculatorError.getMessage(), fieldWithError));

    this.projectCalculatorError = projectCalculatorError;
    this.fieldWithError = fieldWithError;
  }
}
