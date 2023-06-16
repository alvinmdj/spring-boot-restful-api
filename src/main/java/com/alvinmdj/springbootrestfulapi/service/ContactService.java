package com.alvinmdj.springbootrestfulapi.service;

import com.alvinmdj.springbootrestfulapi.entity.User;
import com.alvinmdj.springbootrestfulapi.model.ContactResponse;
import com.alvinmdj.springbootrestfulapi.model.CreateContactRequest;
import com.alvinmdj.springbootrestfulapi.model.SearchContactRequest;
import com.alvinmdj.springbootrestfulapi.model.UpdateContactRequest;
import org.springframework.data.domain.Page;

public interface ContactService {
  ContactResponse create(User user, CreateContactRequest request);

  ContactResponse get(User user, String contactId);

  ContactResponse update(User user, UpdateContactRequest request);

  void delete(User user, String contactId);

  Page<ContactResponse> search(User user, SearchContactRequest request);
}
