package org.example.projectcalculator.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MilestoneDto(
    long id,
    long projectId,
    String title,
    String description,
    LocalDateTime startDateTime,
    LocalDateTime endDateTime,
    BigDecimal estimateInDays,
    BigDecimal priceInRubles,
    LocalDateTime createdAt,
    LocalDateTime lastUpdatedAt) {

}
