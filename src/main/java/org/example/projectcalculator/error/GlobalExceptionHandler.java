package org.example.projectcalculator.error;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.example.projectcalculator.dto.error.ErrorDto;
import org.example.projectcalculator.dto.error.ErrorDtoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorDtoResponse> handleValidationException(
      final MethodArgumentNotValidException e) {
    log.info(e.getMessage());

    final List<ErrorDto> errorsDto = new ArrayList<>();

    for (final var objectError : e.getBindingResult().getGlobalErrors()) {
      errorsDto.add(
          new ErrorDto(
              objectError.getCode(), objectError.getObjectName(), objectError.getDefaultMessage()));
    }

    for (final var fieldError : e.getBindingResult().getFieldErrors()) {
      errorsDto.add(
          new ErrorDto(
              fieldError.getCode(), fieldError.getField(), fieldError.getDefaultMessage()));
    }

    return ResponseEntity.badRequest()
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ErrorDtoResponse(errorsDto));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorDtoResponse> handleConstraintViolationException(
      final ConstraintViolationException e) {
    log.info(e.getMessage());

    final List<ErrorDto> errorsDto = new ArrayList<>();

    for (final var violation : e.getConstraintViolations()) {
      String lastPathNodeName = null;

      for (final Path.Node pathNode : violation.getPropertyPath()) {
        lastPathNodeName = pathNode.getName();
      }

      errorsDto.add(
          new ErrorDto(
              violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
              lastPathNodeName,
              violation.getMessage()));
    }

    return ResponseEntity.badRequest()
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ErrorDtoResponse(errorsDto));
  }

  @ExceptionHandler(ProjectCalculatorException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorDtoResponse> handleProjectCalculatorException(
      final ProjectCalculatorException e) {
    log.info(e.getMessage());

    final List<ErrorDto> errorsDto =
        Collections.singletonList(
            new ErrorDto(
                e.getProjectCalculatorError().name(),
                e.getFieldWithError(),
                e.getProjectCalculatorError().getMessage()));

    return ResponseEntity.badRequest()
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ErrorDtoResponse(errorsDto));
  }

  @ExceptionHandler(Throwable.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorDtoResponse> handleThrowable(final Throwable e) {
    log.warn("Throwable occurred", e);

    final List<ErrorDto> errorsDto =
        Collections.singletonList(new ErrorDto(e.getClass().getName(), null, e.getMessage()));

    return ResponseEntity.internalServerError()
        .contentType(MediaType.APPLICATION_JSON)
        .body(new ErrorDtoResponse(errorsDto));
  }
}
