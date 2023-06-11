package com.alvinmdj.springbootrestfulapi.controller;

import com.alvinmdj.springbootrestfulapi.entity.User;
import com.alvinmdj.springbootrestfulapi.model.ContactResponse;
import com.alvinmdj.springbootrestfulapi.model.CreateContactRequest;
import com.alvinmdj.springbootrestfulapi.model.WebResponse;
import com.alvinmdj.springbootrestfulapi.repository.ContactRepository;
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
class ContactControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ContactRepository contactRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    // always delete all contacts & users before each test
    contactRepository.deleteAll();
    userRepository.deleteAll();

    // always create user before each test
    User user = new User();
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
    user.setName("Test Name");
    user.setToken("test-token");
    user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000L);
    userRepository.save(user);
  }

  @Test
  void testCreateContactBadRequest() throws Exception {
    CreateContactRequest request = new CreateContactRequest();
    request.setFirstName("");
    request.setEmail("wrong-email-format");

    mockMvc.perform(
      post("/api/contacts")
        .accept(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "test-token")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
      status().isBadRequest()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
      });

      assertNotNull(response.getErrors());
    });
  }

  @Test
  void testCreateContactSuccess() throws Exception {
    CreateContactRequest request = new CreateContactRequest();
    request.setFirstName("Alvin");
    request.setLastName("Martin");
    request.setEmail("alvin@google.com");
    request.setPhone("123123123123");

    mockMvc.perform(
      post("/api/contacts")
        .accept(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "test-token")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
      WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertNull(response.getErrors());
      assertEquals("Alvin", response.getData().getFirstName());
      assertEquals("Martin", response.getData().getLastName());
      assertEquals("alvin@google.com", response.getData().getEmail());
      assertEquals("123123123123", response.getData().getPhone());

      // make sure created contact is exists in db
      assertTrue(contactRepository.existsById(response.getData().getId()));
    });
  }
}