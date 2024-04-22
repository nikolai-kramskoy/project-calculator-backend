package org.example.projectcalculator.dto.request.validation.validator;

import java.time.LocalDateTime;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.example.projectcalculator.dto.request.validation.annotation.MilestoneDates;
import org.example.projectcalculator.dto.request.CreateUpdateMilestoneDtoRequest;

public class MilestoneDatesValidator
    implements ConstraintValidator<MilestoneDates, CreateUpdateMilestoneDtoRequest> {

  @Override
  public boolean isValid(
      final CreateUpdateMilestoneDtoRequest request, final ConstraintValidatorContext context) {
    final var start = request.startDateTime();
    final var end = request.endDateTime();

    final var now = LocalDateTime.now(context.getClockProvider().getClock());

    // If start != null, then start must be >= now
    if (start != null && start.isBefore(now)) {
      return false;
    }

    // If end != null, then end must be >= now
    if (end != null && end.isBefore(now)) {
      return false;
    }

    // If start != null and end != null, then end must be >= start
    if (start != null && end != null && end.isBefore(start)) {
      return false;
    }

    // Otherwise dates are valid (start may be == end, I suppose it's okay)
    return true;
  }
}
