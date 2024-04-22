package org.example.projectcalculator.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProjectDto(
    long id,
    String title,
    String description,
    String client,
    long creatorId,
    BigDecimal estimateInDays,
    BigDecimal priceInRubles,
    LocalDateTime createdAt,
    LocalDateTime lastUpdatedAt) {

}
