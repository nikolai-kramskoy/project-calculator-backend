package org.example.projectcalculator.dto.request;

import static org.example.projectcalculator.configuration.ApplicationConfiguration.ESTIMATE_FRACTIONAL_PART;
import static org.example.projectcalculator.configuration.ApplicationConfiguration.ESTIMATE_INTEGER_PART;

import java.math.BigDecimal;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.projectcalculator.dto.request.validation.annotation.FeatureEstimates;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FeatureEstimates
public abstract class CreateUpdateFeatureDtoRequest {

  @NotBlank
  private String title;
  @NotBlank
  private String description;

  @NotNull
  @Digits(integer = ESTIMATE_INTEGER_PART, fraction = ESTIMATE_FRACTIONAL_PART)
  private BigDecimal bestCaseEstimateInDays;

  @NotNull
  @Digits(integer = ESTIMATE_INTEGER_PART, fraction = ESTIMATE_FRACTIONAL_PART)
  private BigDecimal mostLikelyEstimateInDays;

  @NotNull
  @Digits(integer = ESTIMATE_INTEGER_PART, fraction = ESTIMATE_FRACTIONAL_PART)
  private BigDecimal worstCaseEstimateInDays;
}
