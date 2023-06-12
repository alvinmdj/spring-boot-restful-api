package com.alvinmdj.springbootrestfulapi.service;

import com.alvinmdj.springbootrestfulapi.entity.Contact;
import com.alvinmdj.springbootrestfulapi.entity.User;
import com.alvinmdj.springbootrestfulapi.model.ContactResponse;
import com.alvinmdj.springbootrestfulapi.model.CreateContactRequest;
import com.alvinmdj.springbootrestfulapi.model.UpdateContactRequest;
import com.alvinmdj.springbootrestfulapi.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    return toContactResponse(contact);
  }

  @Override
  @Transactional(readOnly = true)
  public ContactResponse get(User user, String contactId) {
    // find contact for current user by contact id
    Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

    // build & return response
    return toContactResponse(contact);
  }

  @Override
  @Transactional
  public ContactResponse update(User user, UpdateContactRequest request) {
    // validate request
    validationService.validate(request);

    // find contact for current user by contact id
    Contact contact = contactRepository.findFirstByUserAndId(user, request.getId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

    // update contact
    contact.setFirstName(request.getFirstName());
    contact.setLastName(request.getLastName());
    contact.setEmail(request.getEmail());
    contact.setPhone(request.getPhone());
    contactRepository.save(contact);

    return toContactResponse(contact);
  }

  private ContactResponse toContactResponse(Contact contact) {
    return ContactResponse.builder()
      .id(contact.getId())
      .firstName(contact.getFirstName())
      .lastName(contact.getLastName())
      .email(contact.getEmail())
      .phone(contact.getPhone())
      .build();
  }
}
