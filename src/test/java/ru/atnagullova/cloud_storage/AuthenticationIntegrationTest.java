package ru.atnagullova.cloud_storage;


import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import ru.atnagullova.cloud_storage.entity.User;
import ru.atnagullova.cloud_storage.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
public class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

    @Container
    @ServiceConnection(name = "redis")
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:latest"))
            .withExposedPorts(6379);


    @Test
    public void signUp_saveUserWithEncodedPassword() throws Exception {

        mockMvc.perform(post("/api/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"username":"ilgiz","password":"password123"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("ilgiz"));

        User savedUser = userRepository.findByUsername("ilgiz").orElseThrow();
        assertThat(savedUser.getPassword()).isNotEqualTo("password123");
        assertThat(passwordEncoder.matches("password123", savedUser.getPassword())).isTrue();
    }

    @Test
    public void signUp_usernameAlreadyExists() throws Exception {

        userRepository.save(new User("tamara", passwordEncoder.encode("password123")));

        mockMvc.perform(post("/api/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"username":"tamara","password":"password456"}
                        """))
                .andExpect(status().isConflict());
    }


    @Test
    public void signIn_wrongPassword() throws Exception {

        userRepository.save(new User("ivan", "password123"));

        mockMvc.perform(post("/api/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"username":"ivan","password":"password456"}
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getMe_withoutAuthentication() throws Exception {

        mockMvc.perform(get("/api/user/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void signIn_andGetMe_returnAuthenticatedUser() throws Exception {

        userRepository.save(new User("petr", passwordEncoder.encode("password123")));

        MvcResult signInResult = mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"petr","password":"password123"}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        Cookie session = signInResult.getResponse().getCookie("SESSION");

        mockMvc.perform(get("/api/user/me").cookie(session)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("petr"));
    }

}
