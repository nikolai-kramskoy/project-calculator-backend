package org.example.projectcalculator.dto.request;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import org.example.projectcalculator.dto.request.validation.annotation.MilestoneDates;

@MilestoneDates
public record CreateUpdateMilestoneDtoRequest(
    @NotBlank String title, @NotBlank String description, LocalDateTime startDateTime,
    LocalDateTime endDateTime) {

}
