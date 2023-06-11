package com.alvinmdj.springbootrestfulapi.service;

import com.alvinmdj.springbootrestfulapi.entity.User;
import com.alvinmdj.springbootrestfulapi.model.ContactResponse;
import com.alvinmdj.springbootrestfulapi.model.CreateContactRequest;

public interface ContactService {
  ContactResponse create(User user, CreateContactRequest request);
}
