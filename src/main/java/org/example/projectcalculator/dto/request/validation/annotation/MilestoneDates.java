package org.example.projectcalculator.dto.request.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import org.example.projectcalculator.dto.request.validation.validator.MilestoneDatesValidator;

@Documented
@Constraint(validatedBy = MilestoneDatesValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MilestoneDates {

  String message() default "milestone dates are invalid";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
