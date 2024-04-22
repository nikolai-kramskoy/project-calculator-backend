package org.example.projectcalculator.service;

import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.example.projectcalculator.model.Position;

/**
 * A {@link Service} that provides operations on {@link Position}.
 */
@Service
@AllArgsConstructor
@Slf4j
public class PositionService {

  private static final List<String> POSITIONS = Arrays.stream(Position.values())
      .map(Position::name)
      .toList();

  /**
   * Returns all {@link Position}s in {@link String} format.
   *
   * @return {@link List} of {@link String}s
   */
  public List<String> getAllPositions() {
    log.info("Get List<Positions>: {}", POSITIONS);

    return POSITIONS;
  }
}
