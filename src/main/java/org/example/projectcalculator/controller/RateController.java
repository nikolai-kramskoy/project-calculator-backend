package org.example.projectcalculator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.example.projectcalculator.dto.RateDto;
import org.example.projectcalculator.dto.request.UpdateRateDtoRequest;
import org.example.projectcalculator.service.RateService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects")
@Tag(name = "rate", description = "the rate API")
@Validated
@AllArgsConstructor
public class RateController {

  private final RateService rateService;

  @GetMapping(
      produces = MediaType.APPLICATION_JSON_VALUE,
      value = "/{projectId}/rates")
  @Operation(summary = "Get project's rates", description = "Get list of all rates of project")
  @ApiResponse(responseCode = "200", description = "Successful query")
  public ResponseEntity<List<RateDto>> getAllRates(
      @PathVariable("projectId") @Min(1) final long projectId) {
    return ResponseEntity.ok(rateService.getAllRates(projectId));
  }

  @PutMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE,
      value = "/{projectId}/rates/{rateId}")
  @Operation(summary = "Update rate", description = "Update rate")
  @ApiResponse(responseCode = "200", description = "Successful update")
  public ResponseEntity<RateDto> updateRate(
      @PathVariable("projectId") @Min(1) final long projectId,
      @PathVariable("rateId") @Min(1) final long rateId,
      @Valid @RequestBody UpdateRateDtoRequest request) {
    return ResponseEntity.ok(rateService.updateRate(request, projectId, rateId));
  }
}
