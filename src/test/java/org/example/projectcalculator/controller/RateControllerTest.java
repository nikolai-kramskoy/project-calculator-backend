package org.example.projectcalculator.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.example.projectcalculator.utility.TestingData.createProject;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.example.projectcalculator.controller.utility.JsonConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.example.projectcalculator.dto.RateDto;
import org.example.projectcalculator.dto.request.UpdateRateDtoRequest;
import org.example.projectcalculator.mapper.RateMapper;
import org.example.projectcalculator.model.Position;
import org.example.projectcalculator.model.Project;
import org.example.projectcalculator.model.Rate;
import org.example.projectcalculator.service.RateService;

@WebMvcTest(RateController.class)
@ComponentScan(basePackageClasses = RateMapper.class)
@WithMockUser
public class RateControllerTest {

  private static final String PROJECT_API_URL = "/projects/1/rates/1";

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  private RateMapper rateMapper;

  @MockBean
  private RateService rateService;


  @Test
  public void testUpdateSuccessful() throws Exception {
    final Project project = createProject(null);
    final Rate rate = new Rate(
        5,
        Position.ARCHITECT,
        new BigDecimal(31),
        project);
    final var updateRateDtoRequest = new UpdateRateDtoRequest(rate.getRublesPerHour());
    final var expectedRateDto = rateMapper.toRateDto(rate);

    when(rateService.updateRate(eq(updateRateDtoRequest), eq(project.getId()),
        eq(rate.getId()))).thenReturn(expectedRateDto);

    final MvcResult result = mockMvc.perform(put(PROJECT_API_URL)
            .with(csrf())
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonConverter.objectToJson(updateRateDtoRequest)))
        .andReturn();

    final RateDto rateDto = JsonConverter.jsonToObject(result.getResponse().getContentAsString(), RateDto.class);
    assertAll(
        () -> assertEquals(200, result.getResponse().getStatus()),
        () -> assertTrue(rateDto.id() > 0),
        () -> assertEquals(rate.getPosition().name(), rateDto.position()),
        () -> assertEquals(updateRateDtoRequest.rublesPerHour(), rateDto.rublesPerHour())
    );
  }

  @Test
  public void testUpdateSuccessful2() throws Exception {
    final Project project = createProject(null);
    final Rate rate = new Rate(
        3,
        Position.REGULAR_DEVELOPER,
        new BigDecimal(23),
        project);

    final var updateRateDtoRequest = new UpdateRateDtoRequest(rate.getRublesPerHour());
    final var expectedRateDto = rateMapper.toRateDto(rate);

    when(rateService.updateRate(eq(updateRateDtoRequest), eq(project.getId()),
        eq(rate.getId()))).thenReturn(expectedRateDto);

    final MvcResult result = mockMvc.perform(put(PROJECT_API_URL)
            .with(csrf())
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonConverter.objectToJson(updateRateDtoRequest)))
        .andReturn();

    final RateDto rateDto = JsonConverter.jsonToObject(result.getResponse().getContentAsString(), RateDto.class);
    assertAll(
        () -> assertEquals(200, result.getResponse().getStatus()),
        () -> assertTrue(rateDto.id() > 0),
        () -> assertEquals(rate.getPosition().name(), rateDto.position()),
        () -> assertEquals(updateRateDtoRequest.rublesPerHour(), rateDto.rublesPerHour())
    );
  }

  @Test
  public void testFailureUpdate1() throws Exception {
    final var failureRequest = new UpdateRateDtoRequest(null);
    final String badPath = "/projects";
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
  public void testFailureUpdate2() throws Exception {
    final var goodRequest = new UpdateRateDtoRequest(new BigDecimal(12));
    final String badPath = "/projectz/22/ratez/123";
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
  public void testBadRequestFailureUpdate() throws Exception {
    final var badRequest = new UpdateRateDtoRequest(null);
    final String goodPath = "/projects/22/rates/123";
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
