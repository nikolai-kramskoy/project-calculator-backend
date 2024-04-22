package org.example.projectcalculator.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record UpdateUserDtoRequest(@NotBlank String password, @NotBlank @Email String email) {

}
