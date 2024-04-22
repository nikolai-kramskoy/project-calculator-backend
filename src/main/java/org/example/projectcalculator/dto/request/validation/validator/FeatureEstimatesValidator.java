package org.example.projectcalculator.dto.request.validation.validator;

import java.math.BigDecimal;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.example.projectcalculator.dto.request.validation.annotation.FeatureEstimates;
import org.example.projectcalculator.dto.request.CreateUpdateFeatureDtoRequest;

public class FeatureEstimatesValidator implements
    ConstraintValidator<FeatureEstimates, CreateUpdateFeatureDtoRequest> {

  @Override
  public boolean isValid(
      final CreateUpdateFeatureDtoRequest request, final ConstraintValidatorContext context) {
    // notation is from this article https://en.wikipedia.org/wiki/Three-point_estimation
    // all feature estimates must be > 0.0 and a <= m <= b

    final var a = request.getBestCaseEstimateInDays();
    final var m = request.getMostLikelyEstimateInDays();
    final var b = request.getWorstCaseEstimateInDays();

    // if a is >= 0.0, then all estimates are >= 0.0, so a >= 0.0 must be true
    if (!(a.compareTo(BigDecimal.ZERO) >= 0)) {
      return false;
    }

    // a <= m <= b must be true
    return a.compareTo(m) <= 0 && m.compareTo(b) <= 0;
  }
}
