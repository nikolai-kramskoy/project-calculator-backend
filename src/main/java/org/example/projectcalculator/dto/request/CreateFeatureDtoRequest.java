package org.example.projectcalculator.dto.request;

import java.math.BigDecimal;
import javax.validation.constraints.Min;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CreateFeatureDtoRequest extends CreateUpdateFeatureDtoRequest {

  // milestoneId is optional, so it's better to use wrapper
  @Min(1)
  private Long milestoneId;

  public CreateFeatureDtoRequest(
      final String title,
      final String description,
      final BigDecimal bestCaseEstimateInDays,
      final BigDecimal mostLikelyEstimateInDays,
      final BigDecimal worstCaseEstimateInDays,
      final Long milestoneId) {
    super(
        title,
        description,
        bestCaseEstimateInDays,
        mostLikelyEstimateInDays,
        worstCaseEstimateInDays);

    this.milestoneId = milestoneId;
  }
}
