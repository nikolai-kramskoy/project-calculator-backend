package org.example.projectcalculator.service;

import static org.example.projectcalculator.utility.TestingData.CLOCK;
import static org.example.projectcalculator.utility.TestingData.RATE_MAPPER;
import static org.example.projectcalculator.utility.TestingData.createProject;
import static org.example.projectcalculator.utility.TestingData.createRate;
import static org.example.projectcalculator.utility.TestingData.createUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.example.projectcalculator.dto.request.UpdateRateDtoRequest;
import org.example.projectcalculator.model.Rate;
import org.example.projectcalculator.repository.RateRepository;
import org.example.projectcalculator.service.utility.ServiceTestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RateServiceTest {

  private UserService userServiceMock;
  private ProjectService projectServiceMock;

  private RateRepository rateRepositoryMock;

  private RateService rateService;

  @BeforeEach
  public void initMocks() {
    userServiceMock = mock(UserService.class);
    projectServiceMock = mock(ProjectService.class);

    rateRepositoryMock = mock(RateRepository.class);

    rateService =
        new RateService(
            userServiceMock,
            projectServiceMock,
            rateRepositoryMock,
            CLOCK,
            RATE_MAPPER);
  }

  @Test
  void testUpdateRate_validRate_returnUpdatedRate() {
    final var creator = createUser();
    final var project = createProject(creator);
    final var rate = createRate(project);
    final var updateRateDtoRequest = new UpdateRateDtoRequest(new BigDecimal("750"));
    final var expectedRateDto = RATE_MAPPER.toRateDto(rate);

    when(rateRepositoryMock.findById(rate.getId())).thenReturn(Optional.of(rate));
    when(rateRepositoryMock.save(any(Rate.class))).thenReturn(rate);

    ServiceTestHelper.setSecurityContext(creator);

    final var actualRateDto = rateService.updateRate(updateRateDtoRequest, project.getId(),
        rate.getId());

    Assertions.assertEquals(expectedRateDto.id(), actualRateDto.id());
    Assertions.assertEquals(expectedRateDto.position(), actualRateDto.position());
    Assertions.assertEquals(expectedRateDto.rublesPerHour(), actualRateDto.rublesPerHour());
  }
}
