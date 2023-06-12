package com.alvinmdj.springbootrestfulapi.controller;

import com.alvinmdj.springbootrestfulapi.entity.User;
import com.alvinmdj.springbootrestfulapi.model.ContactResponse;
import com.alvinmdj.springbootrestfulapi.model.CreateContactRequest;
import com.alvinmdj.springbootrestfulapi.model.UpdateContactRequest;
import com.alvinmdj.springbootrestfulapi.model.WebResponse;
import com.alvinmdj.springbootrestfulapi.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ContactController {
  @Autowired
  private ContactService contactService;

  @PostMapping(
    path = "/api/contacts",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  // requires X-API-TOKEN header value so we need to:
  // set User entity as parameter to trigger UserArgumentResolver.
  public ResponseEntity<WebResponse<ContactResponse>> create(User user, @RequestBody CreateContactRequest request) {
    ContactResponse contactResponse = contactService.create(user, request);

    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(
        WebResponse.<ContactResponse>builder()
          .data(contactResponse)
          .build()
      );
  }

  @GetMapping(
    path = "/api/contacts/{contactId}",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  // requires X-API-TOKEN header value so we need to:
  // set User entity as parameter to trigger UserArgumentResolver.
  public WebResponse<ContactResponse> get(User user, @PathVariable("contactId") String contactId) {
    ContactResponse contactResponse = contactService.get(user, contactId);

    return WebResponse.<ContactResponse>builder()
      .data(contactResponse)
      .build();
  }

  @PutMapping(
    path = "/api/contacts/{contactId}",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  // requires X-API-TOKEN header value so we need to:
  // set User entity as parameter to trigger UserArgumentResolver.
  public WebResponse<ContactResponse> update(
    User user,
    @RequestBody UpdateContactRequest request,
    @PathVariable("contactId") String contactId
  ) {
    request.setId(contactId);

    ContactResponse contactResponse = contactService.update(user, request);

    return WebResponse.<ContactResponse>builder()
      .data(contactResponse)
      .build();
  }
}
