package org.example.projectcalculator.controller.security;

import static org.example.projectcalculator.TestingData.PROJECT_MAPPER;
import static org.example.projectcalculator.TestingData.USER_MAPPER;
import static org.example.projectcalculator.TestingData.createProject;
import static org.example.projectcalculator.TestingData.createUser;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.example.projectcalculator.controller.ProjectController;
import org.example.projectcalculator.controller.UserController;
import org.example.projectcalculator.controller.utility.JsonConverter;
import org.example.projectcalculator.repository.UserRepository;
import org.example.projectcalculator.security.WebSecurityConfiguration;
import org.example.projectcalculator.service.ProjectService;
import org.example.projectcalculator.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(controllers = {UserController.class, ProjectController.class})
@ComponentScan(basePackageClasses = {WebSecurityConfiguration.class})
class AuthenticationTest {

  private static final String USERS_API_URL = "/users";
  private static final String PROJECTS_API_URL = "/projects";

  @Autowired
  private WebApplicationContext context;

  @MockBean
  private UserService userServiceMock;
  @MockBean
  private ProjectService projectServiceMock;

  @MockBean
  private UserRepository userRepositoryMock;

  private MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context)
        .apply(springSecurity())
        .build();
  }

  @Test
  void testCreateProject_noBasicAuth_status401() throws Exception {
    mockMvc
        .perform(
            post(PROJECTS_API_URL)
                .with(csrf())
                .characterEncoding(StandardCharsets.UTF_8))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void testCreateProject_invalidCsrf_status403() throws Exception {
    mockMvc
        .perform(
            post(PROJECTS_API_URL)
                .with(csrf().useInvalidToken())
                .characterEncoding(StandardCharsets.UTF_8))
        .andExpect(status().isForbidden());
  }

  @Test
  void testCreateProject_validBasicCredentials_status200() throws Exception {
    final var creator = createUser();
    final var project = createProject(creator);
    final var createProjectDtoRequest = PROJECT_MAPPER.toCreateProjectDtoRequest(project);
    final var expectedProjectDto = PROJECT_MAPPER.toProjectDto(project);

    when(userRepositoryMock.findByLogin(creator.getLogin())).thenReturn(Optional.of(creator));
    when(projectServiceMock.saveProject(createProjectDtoRequest)).thenReturn(expectedProjectDto);

    mockMvc
        .perform(
            post(PROJECTS_API_URL)
                .with(csrf())
                .with(httpBasic(creator.getLogin(), "qwerty123"))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConverter.objectToJson(createProjectDtoRequest)))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void testCreateProject_invalidBasicCredentials_status401() throws Exception {
    final var creator = createUser();

    when(userRepositoryMock.findByLogin(creator.getLogin())).thenReturn(Optional.of(creator));

    mockMvc
        .perform(
            post(PROJECTS_API_URL)
                .with(csrf())
                .with(httpBasic(creator.getLogin(), "BLAH-BLAH"))
                .characterEncoding(StandardCharsets.UTF_8))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void testCreateUser_validUser_status200() throws Exception {
    final var user = createUser();
    final var createUserDtoRequest = USER_MAPPER.toCreateUserDtoRequest(user, "qwerty123");
    final var expectedUserDto = USER_MAPPER.toUserDto(user);

    when(userServiceMock.saveUser(createUserDtoRequest)).thenReturn(expectedUserDto);

    mockMvc
        .perform(
            post(USERS_API_URL)
                .with(csrf())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConverter.objectToJson(createUserDtoRequest)))
        .andExpect(status().is2xxSuccessful());
  }
}
