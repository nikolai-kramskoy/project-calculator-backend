package org.example.projectcalculator.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.example.projectcalculator.utility.TestingData.CLOCK;
import static org.example.projectcalculator.utility.TestingData.createProject;
import static org.example.projectcalculator.utility.TestingData.createUser;

import java.math.BigDecimal;
import java.util.Optional;
import org.example.projectcalculator.service.utility.ServiceTestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.example.projectcalculator.mapper.RateMapper;
import org.example.projectcalculator.model.Position;
import org.example.projectcalculator.model.Project;
import org.example.projectcalculator.model.Rate;
import org.example.projectcalculator.model.User;
import org.example.projectcalculator.repository.RateRepository;

public class RateServiceTest {

  private UserService userServiceMock;
  private ProjectService projectServiceMock;

  private RateRepository rateRepositoryMock;

  private static final RateMapper RATE_MAPPER = Mappers.getMapper(RateMapper.class);

  private RateService rateService;

  @BeforeEach
  public void initMocks() {
    userServiceMock = mock(UserService.class);
    projectServiceMock = mock(ProjectService.class);

    rateRepositoryMock = mock(RateRepository.class);

    rateService =
        new RateService(
            userServiceMock, projectServiceMock, rateRepositoryMock, CLOCK, RATE_MAPPER);
  }

  @Test
  public void testSuccessfulUpdate1() {
    final long projectId = 321L;
    final long rateId = 1L;
    final User creator = createUser();
    final Project project = createProject(creator);

    final Rate rate =
        new Rate(
            rateId,
            Position.DEVOPS_ENGINEER,
            new BigDecimal(100),
            project);
    final Rate rateFromRepository =
        new Rate(
            rateId,
            rate.getPosition(),
            null,
            project);

    ServiceTestHelper.setSecurityContext(creator);

    when(rateRepositoryMock.findById(anyLong())).thenReturn(Optional.of(rateFromRepository));
    when(rateRepositoryMock.save(rate)).thenReturn(rateFromRepository);

    rate = rateService.updateRate(projectId, rateId, rate);

    Assertions.assertTrue(rate.getId() > 0);
    Assertions.assertEquals(rateFromRepository.getId(), rate.getId());
    Assertions.assertEquals(rateFromRepository.getPosition(), rate.getPosition());
    Assertions.assertEquals(rateFromRepository.getRublesPerHour(), rate.getRublesPerHour());
  }

  @Test
  public void testSuccessfulUpdate2() {
    final long projectId = 321L;
    final long rateId = 1L;
    final User creator = createUser();
    final Project project = createProject(creator);

    final Rate rate =
        new Rate(
            rateId,
            Position.DEVOPS_ENGINEER,
            new BigDecimal(100),
            project);
    final Rate rateFromRepository =
        new Rate(
            rateId,
            rate.getPosition(),
            rate.getRublesPerHour(),
            project);

    ServiceTestHelper.setSecurityContext(creator);

    when(rateRepositoryMock.findById(anyLong())).thenReturn(Optional.of(rateFromRepository));
    when(rateRepositoryMock.save(rate)).thenReturn(rateFromRepository);

    rate = rateService.updateRate(projectId, rateId, rate);

    Assertions.assertTrue(rate.getId() > 0);
    Assertions.assertEquals(rateFromRepository.getId(), rate.getId());
    Assertions.assertEquals(rateFromRepository.getPosition(), rate.getPosition());
    Assertions.assertEquals(rateFromRepository.getRublesPerHour(), rate.getRublesPerHour());
  }
}
