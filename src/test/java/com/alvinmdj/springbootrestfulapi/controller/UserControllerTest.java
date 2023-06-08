package com.alvinmdj.springbootrestfulapi.controller;

import com.alvinmdj.springbootrestfulapi.entity.User;
import com.alvinmdj.springbootrestfulapi.model.RegisterUserRequest;
import com.alvinmdj.springbootrestfulapi.model.UserResponse;
import com.alvinmdj.springbootrestfulapi.model.WebResponse;
import com.alvinmdj.springbootrestfulapi.repository.UserRepository;
import com.alvinmdj.springbootrestfulapi.security.BCrypt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class UserControllerTest {
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
  void testRegisterSuccess() throws Exception {
    RegisterUserRequest request = new RegisterUserRequest();
    request.setUsername("test");
    request.setPassword("password");
    request.setName("Tester");

    mockMvc.perform(
      post("/api/users")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
      status().isCreated()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper
        .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertEquals("OK", response.getData());
    });
  }

  @Test
  void testRegisterBadRequest() throws Exception {
    // Register with bad request.
    RegisterUserRequest request = new RegisterUserRequest();
    request.setUsername("");
    request.setPassword("");
    request.setName("");

    mockMvc.perform(
      post("/api/users")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
      status().isBadRequest()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper
        .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

      assertNotNull(response.getErrors());
    });
  }

  @Test
  void testRegisterDuplicateUsername() throws Exception {
    // Create user.
    User user = new User();
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
    user.setName("Tester 1");
    userRepository.save(user);

    // Register request with existing username.
    RegisterUserRequest request = new RegisterUserRequest();
    request.setUsername("test");
    request.setPassword("password");
    request.setName("Tester 2");

    mockMvc.perform(
      post("/api/users")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
      status().isBadRequest()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper
        .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

      assertNotNull(response.getErrors());
    });
  }

  @Test
  void testGetUserUnauthorizedTokenNotFound() throws Exception {
    mockMvc.perform(
      get("/api/users/current")
        .accept(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "test-token-not-found")
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
  void testGetUserUnauthorizedTokenNotSent() throws Exception {
    mockMvc.perform(
      get("/api/users/current")
        .accept(MediaType.APPLICATION_JSON)
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
  void testGetUserUnauthorizedTokenIsExpired() throws Exception {
    User user = new User();
    user.setName("Test");
    user.setUsername("test");
    user.setPassword("password");
    user.setToken("test-token-expired");
    user.setTokenExpiredAt(System.currentTimeMillis() - 1000000000);
    userRepository.save(user);

    mockMvc.perform(
      get("/api/users/current")
        .accept(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "test-token-expired")
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
  void testGetUserSuccess() throws Exception {
    User user = new User();
    user.setName("Test");
    user.setUsername("test");
    user.setPassword("password");
    user.setToken("test-token");
    user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000L);
    userRepository.save(user);

    mockMvc.perform(
      get("/api/users/current")
        .accept(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "test-token")
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
      WebResponse<UserResponse> response = objectMapper
        .readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });

      assertNull(response.getErrors());
      assertEquals("test", response.getData().getUsername());
      assertEquals("Test", response.getData().getName());
    });
  }
}