package org.example.projectcalculator.service;

import static org.example.projectcalculator.Asserter.assertUsersAreEqual;
import static org.example.projectcalculator.TestingData.CLOCK;
import static org.example.projectcalculator.TestingData.USER_MAPPER;
import static org.example.projectcalculator.TestingData.createUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.example.projectcalculator.error.ProjectCalculatorError;
import org.example.projectcalculator.error.ProjectCalculatorException;
import org.example.projectcalculator.model.User;
import org.example.projectcalculator.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceTest {

  private UserRepository userRepositoryMock;

  private PasswordEncoder passwordEncoderMock;

  private UserService userService;

  @BeforeEach
  public void initMocks() {
    userRepositoryMock = mock(UserRepository.class);

    passwordEncoderMock = mock(PasswordEncoder.class);

    userService = new UserService(userRepositoryMock, passwordEncoderMock, CLOCK, USER_MAPPER);
  }

  @Test
  void testCreateUser_validUser_returnCreatedUser() {
    final var user = createUser();
    final var userPassword = "qwerty123";
    final var createUserDtoRequest = USER_MAPPER.toCreateUserDtoRequest(user, userPassword);
    final var expectedUserDto = USER_MAPPER.toUserDto(user);

    when(userRepositoryMock.findByLogin(user.getLogin())).thenReturn(Optional.empty());
    when(userRepositoryMock.save(any(User.class))).thenReturn(user);

    when(passwordEncoderMock.encode(userPassword)).thenReturn(user.getPasswordHash());

    final var actualUserDto = userService.saveUser(createUserDtoRequest);

    assertUsersAreEqual(expectedUserDto, actualUserDto);
  }

  @Test
  void testCreateUser_loginAlreadyExists_throwException() {
    var user = new User(0L, "someUser", null, "blah@example.com", null, null);
    final var userPassword = "qwerty123";
    final var createUserDtoRequest = USER_MAPPER.toCreateUserDtoRequest(user, userPassword);

    when(userRepositoryMock.findByLogin(user.getLogin())).thenReturn(Optional.of(user));

    final var projectCalculatorException =
        Assertions.assertThrows(
            ProjectCalculatorException.class, () -> userService.saveUser(createUserDtoRequest));

    Assertions.assertEquals(
        ProjectCalculatorError.LOGIN_ALREADY_EXISTS,
        projectCalculatorException.getProjectCalculatorError());
    Assertions.assertEquals("login", projectCalculatorException.getFieldWithError());
  }
}
