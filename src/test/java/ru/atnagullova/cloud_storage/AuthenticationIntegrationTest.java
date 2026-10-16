package ru.atnagullova.cloud_storage;


import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.data.redis.RedisSessionRepository;
import org.springframework.test.web.servlet.MvcResult;
import ru.atnagullova.cloud_storage.entity.User;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AuthenticationIntegrationTest extends AbstractIntegrationTest{


    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RedisSessionRepository sessionRepository;
    @Autowired
    private StringRedisTemplate redisTemplate;


    @Test
    public void signUp_saveUserWithEncodedPassword() throws Exception {

        mockMvc.perform(post("/api/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getContentBody("ilgiz", "password123")))
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
                .content(getContentBody("tamara", "password123")))
                .andExpect(status().isConflict());
    }


    @Test
    public void signIn_wrongPassword() throws Exception {

        userRepository.save(new User("ivan", "password123"));

        mockMvc.perform(post("/api/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getContentBody("ivan", "password456")))
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
                        .content(getContentBody("petr", "password123")))
                .andExpect(status().isOk())
                .andReturn();

        Cookie session = signInResult.getResponse().getCookie("SESSION");

        mockMvc.perform(get("/api/user/me")
                        .cookie(session)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("petr"));
    }

    @Test
    public void signUpAndSignIp_sessionSavedOnRedis() throws Exception {

        Set<String> beforeKeys = redisTemplate.keys("spring:session:sessions:*");
        int countKeysBefore = beforeKeys.size();

        MvcResult result = mockMvc.perform(post("/api/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getContentBody("ilgiz", "password123")))
                .andExpect(status().isCreated())
                .andReturn();

        Cookie session = result.getResponse().getCookie("SESSION");
        assertNotNull(session);

        Set<String> afterKeys = redisTemplate.keys("spring:session:sessions:*");
        int countKeysAfter = afterKeys.size();

        assertTrue(countKeysBefore < countKeysAfter);

        mockMvc.perform(get("/api/resource")
                .cookie(session)
                        .param("path", ""))
                .andExpect(status().isOk());
    }

}
