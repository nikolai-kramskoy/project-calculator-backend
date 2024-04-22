package org.example.projectcalculator.dto.request.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import org.example.projectcalculator.dto.request.validation.validator.FeatureEstimatesValidator;

@Documented
@Constraint(validatedBy = FeatureEstimatesValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FeatureEstimates {

  String message() default "all feature estimates must be > 0.0 and a <= m <= b";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
