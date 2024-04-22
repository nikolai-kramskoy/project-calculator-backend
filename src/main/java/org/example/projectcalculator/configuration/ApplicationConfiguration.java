package org.example.projectcalculator.configuration;

import java.math.RoundingMode;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

  public static final int NUMBER_OF_TEAM_MEMBERS_INTEGER_PART = 12;
  public static final int NUMBER_OF_TEAM_MEMBERS_FRACTIONAL_PART = 2;

  public static final int RUBLES_PER_HOUR_INTEGER_PART = 12;
  public static final int RUBLES_PER_HOUR_FRACTIONAL_PART = 2;

  public static final int ESTIMATE_INTEGER_PART = 12;
  public static final int ESTIMATE_FRACTIONAL_PART = 2;

  public static final RoundingMode ESTIMATE_ROUNDING_MODE = RoundingMode.UP;

  @Bean
  public Clock clock() {
    return Clock.systemUTC();
  }
}
