package com.alvinmdj.springbootrestfulapi.controller;

import com.alvinmdj.springbootrestfulapi.entity.Contact;
import com.alvinmdj.springbootrestfulapi.entity.User;
import com.alvinmdj.springbootrestfulapi.model.ContactResponse;
import com.alvinmdj.springbootrestfulapi.model.CreateContactRequest;
import com.alvinmdj.springbootrestfulapi.model.UpdateContactRequest;
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

import java.util.List;
import java.util.UUID;

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
  void testCreateContactUnauthorizedTokenNotFound() throws Exception {
    mockMvc.perform(
      post("/api/contacts")
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
  void testCreateContactUnauthorizedTokenNotSent() throws Exception {
    mockMvc.perform(
      post("/api/contacts")
        .accept(MediaType.APPLICATION_JSON)
        // no X-API-TOKEN header sent
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
      status().isCreated()
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

  @Test
  void testGetContactUnauthorizedTokenNotFound() throws Exception {
    mockMvc.perform(
      get("/api/contacts/1")
        .accept(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "wrong-test-token")
    ).andExpectAll(
      status().isUnauthorized()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
      });

      assertNotNull(response.getErrors());
    });
  }

  @Test
  void testGetContactUnauthorizedTokenNotSent() throws Exception {
    mockMvc.perform(
      get("/api/contacts/1")
        .accept(MediaType.APPLICATION_JSON)
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
  void testGetContactNotFound() throws Exception {
    mockMvc.perform(
      get("/api/contacts/92138123123")
        .accept(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "test-token")
    ).andExpectAll(
      status().isNotFound()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
      });

      assertNotNull(response.getErrors());
    });
  }

  @Test
  void testGetContactSuccess() throws Exception {
    User user = userRepository.findById("test").orElse(null);

    Contact contact = new Contact();
    contact.setId(UUID.randomUUID().toString());
    contact.setUser(user);
    contact.setFirstName("Alvin");
    contact.setLastName("Martin");
    contact.setEmail("alvin@google.com");
    contact.setPhone("2139213912");
    contactRepository.save(contact);

    mockMvc.perform(
      get("/api/contacts/" + contact.getId())
        .accept(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "test-token")
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
      WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertNull(response.getErrors());
      assertEquals("Alvin", response.getData().getFirstName());
      assertEquals("Martin", response.getData().getLastName());
      assertEquals("alvin@google.com", response.getData().getEmail());
      assertEquals("2139213912", response.getData().getPhone());
    });
  }

  @Test
  void testUpdateContactUnauthorizedTokenNotFound() throws Exception {
    mockMvc.perform(
      put("/api/contacts/1")
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
  void testUpdateContactUnauthorizedTokenNotSent() throws Exception {
    mockMvc.perform(
      put("/api/contacts/1")
        .accept(MediaType.APPLICATION_JSON)
        // no X-API-TOKEN header sent
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
  void testUpdateContactBadRequest() throws Exception {
    UpdateContactRequest request = new UpdateContactRequest();
    request.setFirstName("");
    request.setEmail("wrong-email-format");

    mockMvc.perform(
      put("/api/contacts/1")
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
  void testUpdateContactSuccess() throws Exception {
    // get user
    User user = userRepository.findById("test").orElse(null);

    // create contact
    Contact contact = new Contact();
    contact.setId(UUID.randomUUID().toString());
    contact.setUser(user);
    contact.setFirstName("Alvin");
    contact.setLastName("Martin");
    contact.setEmail("alvin@google.com");
    contact.setPhone("2139213912");
    contactRepository.save(contact);

    // update contact
    UpdateContactRequest request = new UpdateContactRequest();
    request.setFirstName("Dragon");
    request.setLastName("Monkey");
    request.setEmail("monkey@google.com");
    request.setPhone("9999999999");

    mockMvc.perform(
      put("/api/contacts/" + contact.getId())
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
      assertEquals("Dragon", response.getData().getFirstName());
      assertEquals("Monkey", response.getData().getLastName());
      assertEquals("monkey@google.com", response.getData().getEmail());
      assertEquals("9999999999", response.getData().getPhone());

      // make sure created contact is exists in db
      assertTrue(contactRepository.existsById(response.getData().getId()));
    });
  }

  @Test
  void testDeleteContactUnauthorizedTokenNotFound() throws Exception {
    mockMvc.perform(
      delete("/api/contacts/1")
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
  void testDeleteContactUnauthorizedTokenNotSent() throws Exception {
    mockMvc.perform(
      delete("/api/contacts/1")
        .accept(MediaType.APPLICATION_JSON)
        // no X-API-TOKEN header sent
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
  void testDeleteContactNotFound() throws Exception {
    mockMvc.perform(
      delete("/api/contacts/92138123123")
        .accept(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "test-token")
    ).andExpectAll(
      status().isNotFound()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
      });

      assertNotNull(response.getErrors());
    });
  }

  @Test
  void testDeleteContactSuccess() throws Exception {
    User user = userRepository.findById("test").orElse(null);

    Contact contact = new Contact();
    contact.setId(UUID.randomUUID().toString());
    contact.setUser(user);
    contact.setFirstName("Alvin");
    contact.setLastName("Martin");
    contact.setEmail("alvin@google.com");
    contact.setPhone("2139213912");
    contactRepository.save(contact);

    mockMvc.perform(
      delete("/api/contacts/" + contact.getId())
        .accept(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "test-token")
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertNull(response.getErrors());
      assertEquals("OK", response.getData());
    });
  }

  @Test
  void testSearchContactUnauthorizedTokenNotFound() throws Exception {
    mockMvc.perform(
      get("/api/contacts")
        .accept(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "wrong-test-token")
    ).andExpectAll(
      status().isUnauthorized()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
      });

      assertNotNull(response.getErrors());
    });
  }

  @Test
  void testSearchContactUnauthorizedTokenNotSent() throws Exception {
    mockMvc.perform(
      get("/api/contacts")
        .accept(MediaType.APPLICATION_JSON)
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
  void testSearchNotFound() throws Exception {
    mockMvc.perform(
      get("/api/contacts")
        .accept(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "test-token")
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
      WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      assertNull(response.getErrors());
      assertEquals(0, response.getData().size());
      assertEquals(0, response.getPaging().getTotalPage());
      assertEquals(0, response.getPaging().getCurrentPage());
      assertEquals(10, response.getPaging().getSize());
    });
  }

  @Test
  void testSearchSuccess() throws Exception {
    User user = userRepository.findById("test").orElse(null);

    // create 100 contacts with name: Alvin
    for (int i = 0; i < 100; i++) {
      Contact contact = new Contact();
      contact.setId(UUID.randomUUID().toString());
      contact.setUser(user);
      contact.setFirstName("Alvin" + i);
      contact.setLastName("Martin");
      contact.setEmail("alvin@google.com");
      contact.setPhone("2139213912");
      contactRepository.save(contact);
    }

    // search by name: Alvin
    mockMvc.perform(
      get("/api/contacts")
        .accept(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "test-token")
        .queryParam("name", "Alvin")
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
      WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      // should get data (10 data each page), 10 total page,
      // 0 current page (default), and 10 page size (default)
      assertNull(response.getErrors());
      assertEquals(10, response.getData().size());
      assertEquals(10, response.getPaging().getTotalPage());
      assertEquals(0, response.getPaging().getCurrentPage());
      assertEquals(10, response.getPaging().getSize());
    });

    // search by name: Martin
    mockMvc.perform(
      get("/api/contacts")
        .accept(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "test-token")
        .queryParam("name", "Martin")
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
      WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      // should get data (10 data each page), 10 total page,
      // 0 current page (default), and 10 page size (default)
      assertNull(response.getErrors());
      assertEquals(10, response.getData().size());
      assertEquals(10, response.getPaging().getTotalPage());
      assertEquals(0, response.getPaging().getCurrentPage());
      assertEquals(10, response.getPaging().getSize());
    });

    // search by email: alvin@google.com
    mockMvc.perform(
      get("/api/contacts")
        .accept(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "test-token")
        .queryParam("email", "alvin@google.com")
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
      WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      // should get data (10 data each page), 10 total page,
      // 0 current page (default), and 10 page size (default)
      assertNull(response.getErrors());
      assertEquals(10, response.getData().size());
      assertEquals(10, response.getPaging().getTotalPage());
      assertEquals(0, response.getPaging().getCurrentPage());
      assertEquals(10, response.getPaging().getSize());
    });

    // search by phone: 39213 -> only part of the phone number
    mockMvc.perform(
      get("/api/contacts")
        .accept(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "test-token")
        .queryParam("phone", "39213")
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
      WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      // should get data (10 data each page), 10 total page,
      // 0 current page (default), and 10 page size (default)
      assertNull(response.getErrors());
      assertEquals(10, response.getData().size());
      assertEquals(10, response.getPaging().getTotalPage());
      assertEquals(0, response.getPaging().getCurrentPage());
      assertEquals(10, response.getPaging().getSize());
    });

    // search with higher page query param than page number
    mockMvc.perform(
      get("/api/contacts")
        .accept(MediaType.APPLICATION_JSON)
        .header("X-API-TOKEN", "test-token")
        .queryParam("phone", "39213")
        .queryParam("page", "1000")
    ).andExpectAll(
      status().isOk()
    ).andDo(result -> {
      WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
      });

      // should get data (0 data each page), 10 total page,
      // 1000 current page (from query param), and 10 page size (default)
      assertNull(response.getErrors());
      assertEquals(0, response.getData().size());
      assertEquals(10, response.getPaging().getTotalPage());
      assertEquals(1000, response.getPaging().getCurrentPage());
      assertEquals(10, response.getPaging().getSize());
    });
  }
}