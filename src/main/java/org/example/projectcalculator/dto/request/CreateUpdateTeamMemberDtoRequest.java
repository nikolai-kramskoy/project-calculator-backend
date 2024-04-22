package org.example.projectcalculator.dto.request;

import static org.example.projectcalculator.configuration.ApplicationConfiguration.NUMBER_OF_TEAM_MEMBERS_FRACTIONAL_PART;
import static org.example.projectcalculator.configuration.ApplicationConfiguration.NUMBER_OF_TEAM_MEMBERS_INTEGER_PART;

import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

public record CreateUpdateTeamMemberDtoRequest(
    @NotBlank
    String position,
    @Digits(
        integer = NUMBER_OF_TEAM_MEMBERS_INTEGER_PART,
        fraction = NUMBER_OF_TEAM_MEMBERS_FRACTIONAL_PART)
    @DecimalMin(value = "0", inclusive = false)
    BigDecimal numberOfTeamMembers) {

}
