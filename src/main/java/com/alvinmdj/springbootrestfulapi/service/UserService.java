package com.alvinmdj.springbootrestfulapi.service;

import com.alvinmdj.springbootrestfulapi.entity.User;
import com.alvinmdj.springbootrestfulapi.model.RegisterUserRequest;
import com.alvinmdj.springbootrestfulapi.repository.UserRepository;
import com.alvinmdj.springbootrestfulapi.security.BCrypt;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
public class UserService {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private Validator validator;

  @Transactional
  public void register(RegisterUserRequest request) {
    // validate request
    Set<ConstraintViolation<RegisterUserRequest>> constraintViolations = validator.validate(request);
    if (constraintViolations.size() != 0) {
      throw new ConstraintViolationException(constraintViolations);
    }

    // check username availability
    if (userRepository.existsById(request.getUsername())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered");
    }

    // create user
    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
    user.setName(request.getName());

    userRepository.save(user);
  }
}
