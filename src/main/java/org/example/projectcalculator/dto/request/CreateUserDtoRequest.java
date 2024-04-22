package org.example.projectcalculator.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record CreateUserDtoRequest(
    @NotBlank String login, @NotBlank String password, @NotBlank @Email String email) {

}
