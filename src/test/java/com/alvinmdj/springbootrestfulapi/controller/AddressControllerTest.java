package com.alvinmdj.springbootrestfulapi.controller;

import com.alvinmdj.springbootrestfulapi.entity.Contact;
import com.alvinmdj.springbootrestfulapi.entity.User;
import com.alvinmdj.springbootrestfulapi.model.AddressResponse;
import com.alvinmdj.springbootrestfulapi.model.CreateAddressRequest;
import com.alvinmdj.springbootrestfulapi.model.WebResponse;
import com.alvinmdj.springbootrestfulapi.repository.AddressRepository;
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
class AddressControllerTest {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ContactRepository contactRepository;

  @Autowired
  private AddressRepository addressRepository;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    // always delete all before each test
    addressRepository.deleteAll();
    contactRepository.deleteAll();
    userRepository.deleteAll();

    // always create user before each test
    User user = new User();
    user.setUsername("test-user-id");
    user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
    user.setName("Test Name");
    user.setToken("test-token");
    user.setTokenExpiredAt(System.currentTimeMillis() + 1000000000L);
    userRepository.save(user);

    // always create contact before each test
    Contact contact = new Contact();
    contact.setId("test-contact-id");
    contact.setUser(user);
    contact.setFirstName("Alvin");
    contact.setLastName("Martin");
    contact.setEmail("alvin@google.com");
    contact.setPhone("2139213912");
    contactRepository.save(contact);
  }

  @Test
  void testCreateAddressUnauthorizedTokenNotFound() throws Exception {
    mockMvc.perform(
      post("/api/contacts/test-contact-id/addresses")
        .accept(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "wrong-test-token")
        .contentType(MediaType.APPLICATION_JSON)
    ).andExpectAll(
      status().isUnauthorized()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
      });

      assertNotNull(response.getErrors());
    });
  }

  @Test
  void testCreateAddressUnauthorizedTokenNotSent() throws Exception {
    mockMvc.perform(
      post("/api/contacts/test-contact-id/addresses")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        // no X-API-TOKEN header sent
    ).andExpectAll(
      status().isUnauthorized()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
      });

      assertNotNull(response.getErrors());
    });
  }

  @Test
  void testCreateAddressBadRequest() throws Exception {
    CreateAddressRequest request = new CreateAddressRequest();
    request.setCountry("");

    mockMvc.perform(
      post("/api/contacts/test-contact-id/addresses")
        .accept(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "test-token")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
      status().isBadRequest()
    ).andDo(result -> {
      WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertNotNull(response.getErrors());
    });
  }

  @Test
  void testCreateAddressSuccess() throws Exception {
    CreateAddressRequest request = new CreateAddressRequest();
    request.setStreet("Street 1");
    request.setCity("Jakarta");
    request.setProvince("DKI Jakarta");
    request.setCountry("Indonesia");
    request.setPostalCode("12345");

    mockMvc.perform(
      post("/api/contacts/test-contact-id/addresses")
        .accept(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "test-token")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
      WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertNull(response.getErrors());
      assertEquals(request.getStreet(), response.getData().getStreet());
      assertEquals(request.getCity(), response.getData().getCity());
      assertEquals(request.getProvince(), response.getData().getProvince());
      assertEquals(request.getCountry(), response.getData().getCountry());
      assertEquals(request.getPostalCode(), response.getData().getPostalCode());

      assertTrue(addressRepository.existsById(response.getData().getId()));
    });
  }
}