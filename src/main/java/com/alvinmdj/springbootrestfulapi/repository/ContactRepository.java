package com.alvinmdj.springbootrestfulapi.repository;

import com.alvinmdj.springbootrestfulapi.entity.Contact;
import com.alvinmdj.springbootrestfulapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository
  extends JpaRepository<Contact, String>, JpaSpecificationExecutor<Contact> {
  // extends to JpaSpecificationExecutor because we need to implement specs for search contact service.

  Optional<Contact> findFirstByUserAndId(User user, String id);
}
