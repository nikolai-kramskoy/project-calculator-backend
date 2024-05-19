package org.example.projectcalculator.service;

import java.time.Clock;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.projectcalculator.dto.UserDto;
import org.example.projectcalculator.dto.request.CreateUserDtoRequest;
import org.example.projectcalculator.dto.request.UpdateUserDtoRequest;
import org.example.projectcalculator.error.ProjectCalculatorError;
import org.example.projectcalculator.error.ProjectCalculatorException;
import org.example.projectcalculator.mapper.UserMapper;
import org.example.projectcalculator.model.User;
import org.example.projectcalculator.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A {@link Service} that saves {@link User}.
 */
@Service
@AllArgsConstructor
@Slf4j
public class UserService {

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  private final Clock clock;

  private final UserMapper userMapper;

  /**
   * Saves {@link User} in {@link UserRepository}.
   *
   * @param request must be not {@code null}; it's {@code login}, {@code password} and {@code email}
   *                must be not blank (null or size == 0)
   * @return {@link UserDto}
   * @throws ProjectCalculatorException if {@link User} with specified {@code login} already exists
   *                                    in {@link UserRepository}
   */
  @Transactional
  public UserDto saveUser(final CreateUserDtoRequest request) {
    if (userRepository.findByLogin(request.login()).isPresent()) {
      throw new ProjectCalculatorException(ProjectCalculatorError.LOGIN_ALREADY_EXISTS, "login");
    }

    final var now = LocalDateTime.now(clock);

    final var user = userRepository.save(
        userMapper.toUser(request, passwordEncoder.encode(request.password()), now, now));

    log.info("Saved {}", user);

    return userMapper.toUserDto(user);
  }

  /**
   * Returns authenticated {@link User} from {@link UserRepository}.
   *
   * @return {@link UserDto}
   */
  @Transactional(readOnly = true)
  public UserDto getAuthenticatedUser() {
    final var user = getCurrentlyAuthenticatedUser();

    log.info("Get authenticated User: {}", user);

    return userMapper.toUserDto(user);
  }

  /**
   * Updates {@link User} in {@link UserRepository} with data from {@code request}.
   *
   * @param request must be not {@code null}; it's {@code password} and {@code email} must be not
   *                blank (null or size == 0)
   * @param userId  must be {@code > 0}
   * @return {@link UserDto}
   * @throws ProjectCalculatorException if {@link User} with specified {@code userId} is not found
   *                                    in {@link UserRepository}
   */
  @Transactional
  public UserDto updateUser(final UpdateUserDtoRequest request, final long userId) {
    final var user = getCurrentlyAuthenticatedUser();

    if (user.getId() != userId) {
      throw new ProjectCalculatorException(ProjectCalculatorError.USER_IS_NOT_FOUND_BY_ID,
          "userId");
    }

    log.info("Before update {}", user);

    user.setPasswordHash(passwordEncoder.encode(request.password()));
    user.setEmail(request.email());
    user.setLastUpdatedAt(LocalDateTime.now(clock));

    log.info("Updated {}", user);

    return userMapper.toUserDto(user);
  }

  /**
   * Fetches currently authenticated {@link User} from {@link UserRepository} using
   * {@link SecurityContextHolder}.
   *
   * @return current authenticated {@link User}
   * @throws ProjectCalculatorException if {@link User} from {@link SecurityContextHolder} is not
   *                                    found in {@link UserRepository}
   */
  public User getCurrentlyAuthenticatedUser() {
    final var userDetails =
        (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    return userRepository
        .findByLogin(userDetails.getUsername())
        .orElseThrow(
            () -> new ProjectCalculatorException(ProjectCalculatorError.WRONG_LOGIN_OR_PASSWORD));
  }
}
