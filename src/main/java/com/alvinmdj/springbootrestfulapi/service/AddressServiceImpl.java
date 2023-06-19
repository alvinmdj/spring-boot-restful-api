package com.alvinmdj.springbootrestfulapi.service;

import com.alvinmdj.springbootrestfulapi.entity.Address;
import com.alvinmdj.springbootrestfulapi.entity.Contact;
import com.alvinmdj.springbootrestfulapi.entity.User;
import com.alvinmdj.springbootrestfulapi.model.AddressResponse;
import com.alvinmdj.springbootrestfulapi.model.CreateAddressRequest;
import com.alvinmdj.springbootrestfulapi.repository.AddressRepository;
import com.alvinmdj.springbootrestfulapi.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AddressServiceImpl implements AddressService{
  @Autowired
  private ContactRepository contactRepository;

  @Autowired
  private AddressRepository addressRepository;

  @Autowired
  private ValidationService validationService;

  @Override
  public AddressResponse create(User user, CreateAddressRequest request) {
    // validate request
    validationService.validate(request);

    // find contact
    Contact contact = contactRepository.findFirstByUserAndId(user, request.getContactId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

    // create address
    Address address = new Address();
    address.setId(UUID.randomUUID().toString());
    address.setContact(contact);
    address.setStreet(request.getStreet());
    address.setCity(request.getCity());
    address.setProvince(request.getProvince());
    address.setCountry(request.getCountry());
    address.setPostalCode(request.getPostalCode());

    addressRepository.save(address);

    return toAddressResponse(address);
  }

  private AddressResponse toAddressResponse(Address address) {
    return AddressResponse.builder()
      .id(address.getId())
      .street(address.getStreet())
      .city(address.getCity())
      .province(address.getProvince())
      .country(address.getCountry())
      .postalCode(address.getPostalCode())
      .build();
  }
}
