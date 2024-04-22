package org.example.projectcalculator.dto.request;

import static org.example.projectcalculator.configuration.ApplicationConfiguration.RUBLES_PER_HOUR_FRACTIONAL_PART;
import static org.example.projectcalculator.configuration.ApplicationConfiguration.RUBLES_PER_HOUR_INTEGER_PART;

import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

public record UpdateRateDtoRequest(
    @NotNull
    @Digits(integer = RUBLES_PER_HOUR_INTEGER_PART, fraction = RUBLES_PER_HOUR_FRACTIONAL_PART)
    @DecimalMin(value = "0", inclusive = false)
    BigDecimal rublesPerHour) {

}
