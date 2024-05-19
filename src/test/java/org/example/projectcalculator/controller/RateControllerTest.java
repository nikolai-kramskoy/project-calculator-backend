package org.example.projectcalculator.controller;

import static org.example.projectcalculator.Asserter.assertRatesAreEqual;
import static org.example.projectcalculator.TestingData.RATE_MAPPER;
import static org.example.projectcalculator.TestingData.createProject;
import static org.example.projectcalculator.TestingData.createRate;
import static org.example.projectcalculator.TestingData.createUser;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.example.projectcalculator.controller.utility.JsonConverter;
import org.example.projectcalculator.dto.MilestoneDto;
import org.example.projectcalculator.dto.RateDto;
import org.example.projectcalculator.model.Position;
import org.example.projectcalculator.model.Rate;
import org.example.projectcalculator.service.RateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RateController.class)
@WithMockUser
class RateControllerTest {

  private static final String RATES_API_URL = "/projects/{projectId}/rates";
  private static final String SPECIFIC_RATE_API_URL = RATES_API_URL + "/{rateId}";

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  private RateService rateServiceMock;

  @Test
  void testGetAllRates_validArguments_returnList() throws Exception {
    final var creator = createUser();
    final var project = createProject(creator);
    final var expectedRatesDtos =
        List.of(
            RATE_MAPPER.toRateDto(createRate(project)));

    when(rateServiceMock.getAllRates(project.getId())).thenReturn(expectedRatesDtos);

    final var mvcResult =
        mockMvc
            .perform(
                get(RATES_API_URL, project.getId())
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8))
            .andReturn();

    final var response = mvcResult.getResponse();

    Assertions.assertEquals(200, response.getStatus());

    final var actualMilestoneDtos = JsonConverter.jsonToListOfObjects(response.getContentAsString(),
        MilestoneDto.class);

    Assertions.assertEquals(1, actualMilestoneDtos.size());
  }

  @Test
  void testUpdateMilestone_validMilestone_returnMilestoneDto() throws Exception {
    final var creator = createUser();
    final var project = createProject(creator);
    final var rate = createRate(project);
    final var updatedRate = new Rate(rate.getId(), Position.QA_ENGINEER, new BigDecimal("3178"),
        project);
    final var updateRateDtoRequest = RATE_MAPPER.toUpdateRateDtoRequest(updatedRate);
    final var expectedRateDto = RATE_MAPPER.toRateDto(updatedRate);

    when(
        rateServiceMock.updateRate(updateRateDtoRequest, project.getId(), rate.getId())).thenReturn(
        expectedRateDto);

    final var mvcResult =
        mockMvc
            .perform(
                put(SPECIFIC_RATE_API_URL, project.getId(), rate.getId())
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonConverter.objectToJson(updateRateDtoRequest)))
            .andReturn();

    final var response = mvcResult.getResponse();

    Assertions.assertEquals(200, response.getStatus());

    final var actualRateDto = JsonConverter.jsonToObject(response.getContentAsString(),
        RateDto.class);

    assertRatesAreEqual(expectedRateDto, actualRateDto);
  }
}
