package org.example.projectcalculator.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FeatureDto(
    long id,
    long projectId,
    // Long instead of long because milestone is optional, if milestone == null,
    // then milestoneId must be null too because of
    // spring.jackson.default-property-inclusion=non_null
    Long milestoneId,
    String title,
    String description,
    BigDecimal bestCaseEstimateInDays,
    BigDecimal mostLikelyEstimateInDays,
    BigDecimal worstCaseEstimateInDays,
    BigDecimal estimateInDays,
    BigDecimal priceInRubles,
    LocalDateTime createdAt,
    LocalDateTime lastUpdatedAt) {

}
