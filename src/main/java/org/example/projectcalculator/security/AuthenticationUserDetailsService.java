package org.example.projectcalculator.security;

import static org.springframework.security.core.userdetails.User.withUsername;

import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.example.projectcalculator.model.User;
import org.example.projectcalculator.repository.UserRepository;

/**
 * A {@link UserDetailsService} that provides {@link UserDetails} to
 * {@link DaoAuthenticationProvider}.
 */
@Service
@AllArgsConstructor
@Slf4j
public class AuthenticationUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    log.info("trying to authenticate User with username == \"{}\"", username);

    final User user =
        userRepository
            .findByLogin(username)
            .orElseThrow(
                () ->
                    new UsernameNotFoundException(
                        "User with username == \"" + username + "\" is not found"));

    return withUsername(user.getLogin())
        .password(user.getPasswordHash())
        .authorities(Collections.emptyList())
        .build();
  }
}
