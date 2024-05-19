package org.example.projectcalculator.controller.security;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.example.projectcalculator.utility.TestingData.createProject;
import static org.example.projectcalculator.utility.TestingData.createUser;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.example.projectcalculator.controller.utility.JsonConverter;
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
import org.example.projectcalculator.controller.ProjectController;
import org.example.projectcalculator.controller.UserController;
import org.example.projectcalculator.mapper.ProjectMapper;
import org.example.projectcalculator.mapper.UserMapper;
import org.example.projectcalculator.repository.UserRepository;
import org.example.projectcalculator.security.WebSecurityConfiguration;
import org.example.projectcalculator.service.ProjectService;
import org.example.projectcalculator.service.UserService;

@WebMvcTest(controllers = {UserController.class, ProjectController.class})
@ComponentScan(basePackageClasses = {WebSecurityConfiguration.class, UserMapper.class})
class AuthenticationTest {

  private static final String USER_API_URL = "/users";
  private static final String PROJECT_API_URL = "/projects";

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private UserMapper userMapper;
  @Autowired
  private ProjectMapper projectMapper;

  @MockBean
  private UserService userServiceMock;
  @MockBean
  private ProjectService projectServiceMock;

  @MockBean
  private UserRepository userRepositoryMock;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
  }

  @Test
  void testPostCreateProjectEndpoint_noBasicAuth_status401() throws Exception {
    mockMvc
        .perform(post(PROJECT_API_URL).with(csrf()).characterEncoding(StandardCharsets.UTF_8))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void testPostCreateProjectEndpoint_validBasicCredentials_status200() throws Exception {
    final var creator = createUser();
    final var project = createProject(creator);
    final var createProjectDtoRequest = projectMapper.toCreateProjectDtoRequest(project);
    final var expectedProjectDto = projectMapper.toProjectDto(project);

    when(userRepositoryMock.findByLogin(creator.getLogin())).thenReturn(Optional.of(creator));
    when(projectServiceMock.saveProject(createProjectDtoRequest)).thenReturn(expectedProjectDto);

    mockMvc
        .perform(
            post(PROJECT_API_URL)
                .with(csrf())
                .with(httpBasic(creator.getLogin(), "qwerty123"))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConverter.objectToJson(createProjectDtoRequest)))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void testPostCreateProjectEndpoint_invalidBasicCredentials_status401() throws Exception {
    final var creator = createUser();

    when(userRepositoryMock.findByLogin(creator.getLogin())).thenReturn(Optional.of(creator));

    mockMvc
        .perform(
            post(PROJECT_API_URL)
                .with(csrf())
                .with(httpBasic(creator.getLogin(), "BLAH-BLAH"))
                .characterEncoding(StandardCharsets.UTF_8))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void testPostCreateUserEndpoint_noCredentials_status200() throws Exception {
    final var user = createUser();
    final var createUserDtoRequest = userMapper.toCreateUserDtoRequest(user, "qwerty123");
    final var expectedUserDto = userMapper.toUserDto(user);

    when(userServiceMock.saveUser(createUserDtoRequest)).thenReturn(expectedUserDto);

    mockMvc
        .perform(
            post(USER_API_URL)
                .with(csrf())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConverter.objectToJson(createUserDtoRequest)))
        .andExpect(status().is2xxSuccessful());
  }
}
