package com.alvinmdj.springbootrestfulapi.service;

import com.alvinmdj.springbootrestfulapi.entity.Contact;
import com.alvinmdj.springbootrestfulapi.entity.User;
import com.alvinmdj.springbootrestfulapi.model.ContactResponse;
import com.alvinmdj.springbootrestfulapi.model.CreateContactRequest;
import com.alvinmdj.springbootrestfulapi.model.SearchContactRequest;
import com.alvinmdj.springbootrestfulapi.model.UpdateContactRequest;
import com.alvinmdj.springbootrestfulapi.repository.ContactRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

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

  @Override
  @Transactional
  public void delete(User user, String contactId) {
    // find contact for current user by contact id
    Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

    // delete contact if exists
    contactRepository.delete(contact);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ContactResponse> search(User user, SearchContactRequest request) {
    // create specs for search query params
    Specification<Contact> specification = (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      // user must be equal
      predicates.add(criteriaBuilder.equal(root.get("user"), user));

      // if request name exists, add predicate for search by name
      if (Objects.nonNull(request.getName())) {
        // add with OR to look for both LIKE first name & LIKE last name
        predicates.add(criteriaBuilder.or(
          criteriaBuilder.like(root.get("firstName"), "%" + request.getName() + "%"),
          criteriaBuilder.like(root.get("lastName"), "%" + request.getName() + "%")
        ));
      }

      // if request email exists, add predicate for search LIKE email
      if (Objects.nonNull(request.getEmail())) {
        predicates.add(criteriaBuilder.like(root.get("email"), "%" + request.getEmail() + "%"));
      }

      // if request phone exists, add predicate for search LIKE phone
      if (Objects.nonNull(request.getPhone())) {
        predicates.add(criteriaBuilder.like(root.get("phone"), "%" + request.getPhone() + "%"));
      }

      // return query specification
      return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
    };

    // create pageable request
    Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

    // findAll will return Page<Contact>, but we need List<ContactResponse>
    Page<Contact> contacts = contactRepository.findAll(specification, pageable);

    // convert contacts type to List<ContactResponse>
    List<ContactResponse> contactResponses = contacts.getContent().stream()
      .map(this::toContactResponse)
      .toList();

    // return PageImpl not Page because Page is an interface
    // return the contact responses, pageable, and total contact data
    return new PageImpl<>(contactResponses, pageable, contacts.getTotalElements());
  }
}
