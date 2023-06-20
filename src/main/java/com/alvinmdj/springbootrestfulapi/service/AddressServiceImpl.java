package com.alvinmdj.springbootrestfulapi.service;

import com.alvinmdj.springbootrestfulapi.entity.Address;
import com.alvinmdj.springbootrestfulapi.entity.Contact;
import com.alvinmdj.springbootrestfulapi.entity.User;
import com.alvinmdj.springbootrestfulapi.model.AddressResponse;
import com.alvinmdj.springbootrestfulapi.model.ContactResponse;
import com.alvinmdj.springbootrestfulapi.model.CreateAddressRequest;
import com.alvinmdj.springbootrestfulapi.model.UpdateAddressRequest;
import com.alvinmdj.springbootrestfulapi.repository.AddressRepository;
import com.alvinmdj.springbootrestfulapi.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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
  @Transactional
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

  @Override
  @Transactional(readOnly = true)
  public AddressResponse get(User user, String contactId, String addressId) {
    // find contact
    Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

    // find address
    Address address = addressRepository.findFirstByContactAndId(contact, addressId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));;

    return toAddressResponse(address);
  }

  @Override
  @Transactional
  public AddressResponse update(User user, UpdateAddressRequest request) {
    // validate request
    validationService.validate(request);

    // find contact
    Contact contact = contactRepository.findFirstByUserAndId(user, request.getContactId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

    // find address
    Address address = addressRepository.findFirstByContactAndId(contact, request.getAddressId())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

    // update address
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

  @Override
  @Transactional
  public void delete(User user, String contactId, String addressId) {
    // find contact
    Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

    // find address
    Address address = addressRepository.findFirstByContactAndId(contact, addressId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

    addressRepository.delete(address);
  }

  @Override
  @Transactional(readOnly = true)
  public List<AddressResponse> list(User user, String contactId) {
    // find contact
    Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

    // find all addresses by contact
    List<Address> addresses = addressRepository.findAllByContact(contact);

    // convert addresses type to List<AddressResponse>
    return addresses.stream()
      // .map(address -> toAddressResponse(address)) // SAME AS BELOW
      .map(this::toAddressResponse)
      .toList();
  }
}
