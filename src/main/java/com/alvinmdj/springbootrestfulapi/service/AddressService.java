package com.alvinmdj.springbootrestfulapi.service;

import com.alvinmdj.springbootrestfulapi.entity.User;
import com.alvinmdj.springbootrestfulapi.model.AddressResponse;
import com.alvinmdj.springbootrestfulapi.model.CreateAddressRequest;

public interface AddressService {
  AddressResponse create(User user, CreateAddressRequest request);

  AddressResponse get(User user, String contactId, String addressId);
}
