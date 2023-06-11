package com.alvinmdj.springbootrestfulapi.service;

import com.alvinmdj.springbootrestfulapi.entity.Contact;
import com.alvinmdj.springbootrestfulapi.entity.User;
import com.alvinmdj.springbootrestfulapi.model.ContactResponse;
import com.alvinmdj.springbootrestfulapi.model.CreateContactRequest;
import com.alvinmdj.springbootrestfulapi.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ContactServiceImpl implements ContactService {
  @Autowired
  private ContactRepository contactRepository;

  @Autowired
  private ValidationService validationService;

  @Override
  @Transactional
  public ContactResponse create(User user, CreateContactRequest request) {
    // validate request
    validationService.validate(request);

    // create contact
    Contact contact = new Contact();
    contact.setId(UUID.randomUUID().toString());
    contact.setFirstName(request.getFirstName());
    contact.setLastName(request.getLastName());
    contact.setEmail(request.getEmail());
    contact.setPhone(request.getPhone());
    contact.setUser(user);

    // save to db
    contactRepository.save(contact);

    // build & return response
    return ContactResponse.builder()
      .id(contact.getId())
      .firstName(contact.getFirstName())
      .lastName(contact.getLastName())
      .email(contact.getEmail())
      .phone(contact.getPhone())
      .build();
  }
}
