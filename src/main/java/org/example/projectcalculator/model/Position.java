package org.example.projectcalculator.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum Position {
  REGULAR_DEVELOPER(new BigDecimal(1600)),
  SENIOR_DEVELOPER(new BigDecimal(2400)),
  PROJECT_MANAGER(new BigDecimal(1760)),
  QA_ENGINEER(new BigDecimal(2000)),
  ARCHITECT(new BigDecimal(2800)),
  DEVOPS_ENGINEER(new BigDecimal(1600));

  private final BigDecimal defaultRateInRublesPerHour;
}
