package org.example.projectcalculator.dto;

import java.time.LocalDateTime;

public record UserDto(
    long id,
    String login,
    String email,
    LocalDateTime createdAt,
    LocalDateTime lastUpdatedAt) {

}
