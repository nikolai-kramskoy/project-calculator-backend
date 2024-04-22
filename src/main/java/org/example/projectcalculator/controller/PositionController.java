package org.example.projectcalculator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.example.projectcalculator.service.PositionService;

@RestController
@RequestMapping("/positions")
@Tag(name = "position", description = "the positions API")
@Validated
@AllArgsConstructor
public class PositionController {

  private final PositionService positionService;

  @GetMapping(
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get positions", description = "Get list of all positions")
  @ApiResponse(responseCode = "200", description = "Successful query")
  public ResponseEntity<List<String>> getAllPositions() {
    return ResponseEntity.ok(positionService.getAllPositions());
  }
}
