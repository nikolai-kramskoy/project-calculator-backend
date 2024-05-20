package org.example.projectcalculator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.example.projectcalculator.dto.UserDto;
import org.example.projectcalculator.dto.request.CreateUserDtoRequest;
import org.example.projectcalculator.dto.request.UpdateUserDtoRequest;
import org.example.projectcalculator.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Tag(name = "Users")
@Validated
@AllArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Create a user", description = "Create a user")
  @ApiResponse(responseCode = "200", description = "Successful creation")
  public ResponseEntity<UserDto> createUser(
      @Valid @RequestBody final CreateUserDtoRequest request) {
    return ResponseEntity.ok(userService.createUser(request));
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Get authenticated user", description = "Get authenticated user")
  @ApiResponse(responseCode = "200", description = "Successful query")
  public ResponseEntity<UserDto> getAuthenticatedUser() {
    return ResponseEntity.ok(userService.getAuthenticatedUser());
  }

  @PutMapping(
      path = "/{userId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Update user", description = "Update user")
  @ApiResponse(responseCode = "200", description = "Successful update")
  public ResponseEntity<UserDto> updateUser(
      @Valid @RequestBody final UpdateUserDtoRequest request,
      @PathVariable("userId") @Min(1) final long userId) {
    return ResponseEntity.ok(userService.updateUser(request, userId));
  }
}
