package org.example.projectcalculator.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.example.projectcalculator.utility.Asserter.assertUsersAreEqual;
import static org.example.projectcalculator.utility.TestingData.CLOCK;
import static org.example.projectcalculator.utility.TestingData.createUser;

import java.util.Optional;
import org.example.projectcalculator.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.example.projectcalculator.dto.UserDto;
import org.example.projectcalculator.error.ProjectCalculatorError;
import org.example.projectcalculator.error.ProjectCalculatorException;
import org.example.projectcalculator.mapper.UserMapper;
import org.example.projectcalculator.model.User;
import org.example.projectcalculator.repository.UserRepository;

public class UserServiceTest {

  private UserRepository userRepositoryMock;

  private PasswordEncoder passwordEncoderMock;

  private static final UserMapper USER_MAPPER = Mappers.getMapper(UserMapper.class);

  private UserService userService;

  @BeforeEach
  public void initMocks() {
    userRepositoryMock = mock(UserRepository.class);

    passwordEncoderMock = mock(PasswordEncoder.class);

    userService = new UserService(userRepositoryMock, passwordEncoderMock, CLOCK, USER_MAPPER);
  }

  @Test
  public void testCreateUser_validUser_returnCreatedUser() {
    // Arrange

    final User user = createUser();
    final String userPassword = "qwerty123";
    final var createUserDtoRequest = USER_MAPPER.toCreateUserDtoRequest(user, userPassword);
    final var expectedUserDto = USER_MAPPER.toUserDto(user);

    when(userRepositoryMock.findByLogin(eq(user.getLogin()))).thenReturn(Optional.empty());
    when(userRepositoryMock.save(any(User.class))).thenReturn(user);

    when(passwordEncoderMock.encode(eq(userPassword)))
        .thenReturn(user.getPasswordHash());

    // Act

    final UserDto actualUserDto = userService.saveUser(createUserDtoRequest);

    // Assert

    assertUsersAreEqual(expectedUserDto, actualUserDto);
  }

  @Test
  public void testCreateUser_loginAlreadyExists_throwException() {
    // Arrange

    User user = new User(0L, "someUser", null, "blah@example.com", null, null);
    final String userPassword = "qwerty123";
    final var createUserDtoRequest = USER_MAPPER.toCreateUserDtoRequest(user, userPassword);

    when(userRepositoryMock.findByLogin(eq(user.getLogin()))).thenReturn(Optional.of(user));

    // Act, assert

    final var projectCalculatorException =
        Assertions.assertThrows(
            ProjectCalculatorException.class, () -> userService.saveUser(createUserDtoRequest));

    Assertions.assertEquals(
        ProjectCalculatorError.LOGIN_ALREADY_EXISTS,
        projectCalculatorException.getProjectCalculatorError());
    Assertions.assertEquals("login", projectCalculatorException.getFieldWithError());
  }
}
