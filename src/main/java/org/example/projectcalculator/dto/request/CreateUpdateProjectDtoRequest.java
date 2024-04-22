package org.example.projectcalculator.dto.request;

import javax.validation.constraints.NotBlank;

public record CreateUpdateProjectDtoRequest(
    @NotBlank
    String title,

    @NotBlank
    String description,

    @NotBlank
    String client) {

}
