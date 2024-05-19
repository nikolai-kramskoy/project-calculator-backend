package org.example.projectcalculator.controller;

import static org.example.projectcalculator.utility.TestingData.createProject;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.example.projectcalculator.controller.utility.JsonConverter;
import org.example.projectcalculator.dto.RateDto;
import org.example.projectcalculator.dto.request.UpdateRateDtoRequest;
import org.example.projectcalculator.mapper.RateMapper;
import org.example.projectcalculator.model.Position;
import org.example.projectcalculator.model.Rate;
import org.example.projectcalculator.service.RateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RateController.class)
@ComponentScan(basePackageClasses = RateMapper.class)
@WithMockUser
class RateControllerTest {

  private static final String PROJECT_API_URL = "/projects/1/rates/1";

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  private RateMapper rateMapper;

  @MockBean
  private RateService rateService;

  @Test
  void testUpdateSuccessful() throws Exception {
    final var project = createProject(null);
    final var rate = new Rate(
        5,
        Position.ARCHITECT,
        new BigDecimal(31),
        project);
    final var updateRateDtoRequest = new UpdateRateDtoRequest(rate.getRublesPerHour());
    final var expectedRateDto = rateMapper.toRateDto(rate);

    when(rateService.updateRate(updateRateDtoRequest, project.getId(), rate.getId())).thenReturn(
        expectedRateDto);

    final var result = mockMvc.perform(put(PROJECT_API_URL)
            .with(csrf())
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonConverter.objectToJson(updateRateDtoRequest)))
        .andReturn();

    final var rateDto = JsonConverter.jsonToObject(result.getResponse().getContentAsString(),
        RateDto.class);

    assertAll(
        () -> assertEquals(200, result.getResponse().getStatus()),
        () -> assertTrue(rateDto.id() > 0),
        () -> assertEquals(rate.getPosition().name(), rateDto.position()),
        () -> assertEquals(updateRateDtoRequest.rublesPerHour(), rateDto.rublesPerHour())
    );
  }

  @Test
  void testUpdateSuccessful2() throws Exception {
    final var project = createProject(null);
    final var rate = new Rate(
        3,
        Position.REGULAR_DEVELOPER,
        new BigDecimal(23),
        project);

    final var updateRateDtoRequest = new UpdateRateDtoRequest(rate.getRublesPerHour());
    final var expectedRateDto = rateMapper.toRateDto(rate);

    when(rateService.updateRate(updateRateDtoRequest, project.getId(), rate.getId())).thenReturn(
        expectedRateDto);

    final var result = mockMvc.perform(put(PROJECT_API_URL)
            .with(csrf())
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonConverter.objectToJson(updateRateDtoRequest)))
        .andReturn();

    final var rateDto = JsonConverter.jsonToObject(result.getResponse().getContentAsString(),
        RateDto.class);

    assertAll(
        () -> assertEquals(200, result.getResponse().getStatus()),
        () -> assertTrue(rateDto.id() > 0),
        () -> assertEquals(rate.getPosition().name(), rateDto.position()),
        () -> assertEquals(updateRateDtoRequest.rublesPerHour(), rateDto.rublesPerHour())
    );
  }

  @Test
  void testFailureUpdate1() throws Exception {
    final var failureRequest = new UpdateRateDtoRequest(null);
    final var badPath = "/projects";

    mockMvc
        .perform(
            put(badPath)
                .with(csrf())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConverter.objectToJson(failureRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  void testFailureUpdate2() throws Exception {
    final var goodRequest = new UpdateRateDtoRequest(new BigDecimal(12));
    final var badPath = "/projectz/22/ratez/123";

    mockMvc
        .perform(
            put(badPath)
                .with(csrf())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConverter.objectToJson(goodRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  void testBadRequestFailureUpdate() throws Exception {
    final var badRequest = new UpdateRateDtoRequest(null);
    final var goodPath = "/projects/22/rates/123";

    mockMvc
        .perform(
            put(goodPath)
                .with(csrf())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConverter.objectToJson(badRequest)))
        .andExpect(status().isBadRequest());
  }
}
