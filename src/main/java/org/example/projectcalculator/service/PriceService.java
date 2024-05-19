package org.example.projectcalculator.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.projectcalculator.model.Position;
import org.example.projectcalculator.model.Project;
import org.example.projectcalculator.model.Rate;
import org.example.projectcalculator.model.TeamMember;
import org.springframework.stereotype.Service;

/**
 * A {@link Service} that computes prices.
 */
@Service
@AllArgsConstructor
@Slf4j
public class PriceService {

  /**
   * Computes price in RUB based on {@code teamMembers} of given {@link Project} and
   * {@code estimateInDays} for all {@link Position}s.
   *
   * @param project        must be not {@code null}
   * @param estimateInDays must be not {@code null}
   * @return {@link BigDecimal}
   */
  public BigDecimal computePriceInRubles(final Project project, final BigDecimal estimateInDays) {
    final var rates = project.getRates();
    final var teamMembers = project.getTeamMembers();

    final var teamPrice = Arrays.stream(Position.values())
        .map(position -> getPositionPrice(rates, teamMembers, position))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    final var hoursInDay = BigDecimal.valueOf(8);

    return estimateInDays.multiply(teamPrice).multiply(hoursInDay);
  }

  /**
   * Computes price in RUB for given {@link Position} based on {@code rates} and
   * {@code teamMembers}.
   *
   * @param rates       must be not {@code null}
   * @param teamMembers must be not {@code null}
   * @param position    must be not {@code null}
   * @return {@link BigDecimal}
   */
  private BigDecimal getPositionPrice(final List<Rate> rates, final List<TeamMember> teamMembers,
      final Position position) {
    return getRateInRublesPerHour(rates, position).multiply(
        getDegreeOfInvolvement(teamMembers, position));
  }

  private BigDecimal getRateInRublesPerHour(final List<Rate> rates, final Position position) {
    return rates.stream()
        .filter(rate -> rate.getPosition() == position)
        .map(Rate::getRublesPerHour)
        .findAny()
        .orElseThrow(() -> new AssertionError("there must be " + position));
  }

  private BigDecimal getDegreeOfInvolvement(final List<TeamMember> teamMembers,
      final Position position) {
    return teamMembers.stream()
        .filter(rate -> rate.getPosition() == position)
        .map(TeamMember::getNumberOfTeamMembers)
        .findAny()
        .orElse(BigDecimal.ZERO);
  }
}
