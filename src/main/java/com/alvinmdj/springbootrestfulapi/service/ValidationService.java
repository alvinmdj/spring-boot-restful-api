package com.alvinmdj.springbootrestfulapi.service;

import com.alvinmdj.springbootrestfulapi.model.RegisterUserRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ValidationService {
  @Autowired
  private Validator validator;

  // throw exception if request doesn't conforms with the model validation
  public void validate(Object request) {
    Set<ConstraintViolation<Object>> constraintViolations = validator.validate(request);
    if (constraintViolations.size() != 0) {
      throw new ConstraintViolationException(constraintViolations);
    }
  }
}
