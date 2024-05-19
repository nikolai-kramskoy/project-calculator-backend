package org.example.projectcalculator.security;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.Arrays;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
      @Value("${CORS_ORIGINS:#{null}}") final String corsOrigins) throws Exception {
    http = http.authorizeHttpRequests(
            authorize ->
                authorize
                    // Swagger and actuator aren't under authentication or authorization
                    .mvcMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",

                        "/actuator/**")
                    .permitAll()

                    // User creation isn't under authentication or authorization
                    .mvcMatchers(HttpMethod.POST, "/users")
                    .permitAll()

                    // Get all Positions endpoint isn't under authentication or authorization
                    .mvcMatchers(HttpMethod.GET, "/positions")
                    .permitAll()

                    // Other requests are under authentication and authorization
                    .anyRequest()
                    .authenticated())
        // In production CSRF protection must be on
        .csrf(AbstractHttpConfigurer::disable)
        .httpBasic(withDefaults());

    if (corsOrigins != null) {
      http = http.cors(withDefaults());
    }

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  @ConditionalOnProperty(name = "CORS_ORIGINS")
  public CorsConfigurationSource corsConfigurationSource(
      @Value("${CORS_ORIGINS}") final String corsOrigins) {
    final var configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(corsOrigins.split(" ")));
    configuration.setAllowedMethods(Collections.singletonList("*"));
    configuration.setAllowedHeaders(Collections.singletonList("*"));
    configuration.setAllowCredentials(true);

    final var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}
