package org.example.projectcalculator.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.example.projectcalculator.controller.utility.JsonConverter.jsonToObject;
import static org.example.projectcalculator.controller.utility.JsonConverter.objectToJson;
import static org.example.projectcalculator.utility.Asserter.assertUsersAreEqual;
import static org.example.projectcalculator.utility.Asserter.assertValidationError;
import static org.example.projectcalculator.utility.TestingData.createUser;

import java.nio.charset.StandardCharsets;
import javax.validation.constraints.NotBlank;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.example.projectcalculator.dto.UserDto;
import org.example.projectcalculator.dto.error.ErrorDtoResponse;
import org.example.projectcalculator.dto.request.CreateUserDtoRequest;
import org.example.projectcalculator.mapper.UserMapper;
import org.example.projectcalculator.model.User;
import org.example.projectcalculator.service.UserService;

@WebMvcTest(UserController.class)
@ComponentScan(basePackageClasses = UserMapper.class)
@WithMockUser
public class UserControllerTest {

  private static final String USER_API_URL = "/users";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserMapper userMapper;

  @MockBean
  private UserService userServiceMock;

  @Test
  public void testCreateUser_validUser_returnUserDto() throws Exception {
    // Arrange

    final User user = createUser();
    final var createUserDtoRequest = userMapper.toCreateUserDtoRequest(user, "qwerty123");
    final var expectedUserDto = userMapper.toUserDto(user);

    when(userServiceMock.saveUser(eq(createUserDtoRequest))).thenReturn(expectedUserDto);

    // Act

    final MvcResult mvcResult =
        mockMvc
            .perform(
                post(USER_API_URL)
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectToJson(createUserDtoRequest)))
            .andReturn();

    // Assert

    final var response = mvcResult.getResponse();
    Assertions.assertEquals(200, response.getStatus());

    final var actualUserDto = jsonToObject(response.getContentAsString(), UserDto.class);
    assertUsersAreEqual(expectedUserDto, actualUserDto);
  }

  @Test
  public void testCreateUser_invalidUser_returnErrorDtoResponse() throws Exception {
    // Arrange

    final var createUserDtoRequest = new CreateUserDtoRequest(null, null, null);

    // Act

    final MvcResult mvcResult =
        mockMvc
            .perform(
                post(USER_API_URL)
                    .with(csrf())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectToJson(createUserDtoRequest)))
            .andReturn();

    // Assert

    final var response = mvcResult.getResponse();
    Assertions.assertEquals(400, response.getStatus());

    final var errorDtoResponse =
        jsonToObject(response.getContentAsString(), ErrorDtoResponse.class);
    // Must be 3 validation errors
    Assertions.assertEquals(3, errorDtoResponse.errors().size());

    final String notBlankErrorCode = NotBlank.class.getSimpleName();
    final String notBlankErrorMessage = "must not be blank";

    final var errors = errorDtoResponse.errors();
    assertValidationError(errors, "login", notBlankErrorCode, notBlankErrorMessage);
    assertValidationError(errors, "password", notBlankErrorCode, notBlankErrorMessage);
    assertValidationError(errors, "email", notBlankErrorCode, notBlankErrorMessage);
  }
}
