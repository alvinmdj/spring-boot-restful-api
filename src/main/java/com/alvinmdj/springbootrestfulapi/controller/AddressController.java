package com.alvinmdj.springbootrestfulapi.controller;

import com.alvinmdj.springbootrestfulapi.entity.User;
import com.alvinmdj.springbootrestfulapi.model.AddressResponse;
import com.alvinmdj.springbootrestfulapi.model.CreateAddressRequest;
import com.alvinmdj.springbootrestfulapi.model.WebResponse;
import com.alvinmdj.springbootrestfulapi.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AddressController {
  @Autowired
  private AddressService addressService;

  @PostMapping(
    path = "/api/contacts/{contactId}/addresses",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  // requires X-API-TOKEN header value so we need to:
  // set User entity as parameter to trigger UserArgumentResolver.
  public WebResponse<AddressResponse> create(
    User user,
    @RequestBody CreateAddressRequest request,
    @PathVariable("contactId") String contactId
  ) {
    request.setContactId(contactId);

    AddressResponse addressResponse = addressService.create(user, request);

    return WebResponse.<AddressResponse>builder()
      .data(addressResponse)
      .build();
  }
}
