package com.alvinmdj.springbootrestfulapi.controller;

import com.alvinmdj.springbootrestfulapi.entity.User;
import com.alvinmdj.springbootrestfulapi.model.LoginUserRequest;
import com.alvinmdj.springbootrestfulapi.model.TokenResponse;
import com.alvinmdj.springbootrestfulapi.model.WebResponse;
import com.alvinmdj.springbootrestfulapi.repository.UserRepository;
import com.alvinmdj.springbootrestfulapi.security.BCrypt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
  }

  @Test
  void loginFailedUserNotFound() throws Exception {
    LoginUserRequest request = LoginUserRequest.builder()
      .username("hello")
      .password("password")
      .build();

    mockMvc.perform(
      post("/api/auth/login")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
      status().isUnauthorized()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper
        .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

      assertNotNull(response.getErrors());
    });
  }

  @Test
  void loginFailedWrongPassword() throws Exception {
    // create user
    User user = new User();
    user.setName("Test");
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
    userRepository.save(user);

    LoginUserRequest request = LoginUserRequest.builder()
      .username("test")
      .password("wrong-password")
      .build();

    mockMvc.perform(
      post("/api/auth/login")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
      status().isUnauthorized()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper
        .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

      assertNotNull(response.getErrors());
    });
  }

  @Test
  void loginSuccess() throws Exception {
    // create user
    User user = new User();
    user.setName("Test");
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
    userRepository.save(user);

    LoginUserRequest request = LoginUserRequest.builder()
      .username("test")
      .password("password")
      .build();

    mockMvc.perform(
      post("/api/auth/login")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
      WebResponse<TokenResponse> response = objectMapper
        .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

      assertNull(response.getErrors());
      assertNotNull(response.getData().getToken());
      assertNotNull(response.getData().getExpiredAt());

      // make sure token stored in db equals to token sent to response
      User userDb = userRepository.findById("test").orElse(null);
      assertNotNull(userDb);
      assertEquals(response.getData().getToken(), userDb.getToken());
      assertEquals(response.getData().getExpiredAt(), userDb.getTokenExpiredAt());
    });
  }
}