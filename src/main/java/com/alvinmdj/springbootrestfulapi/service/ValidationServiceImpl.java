package com.alvinmdj.springbootrestfulapi.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ValidationServiceImpl implements ValidationService {
  @Autowired
  private Validator validator;

  // throw exception if request doesn't conforms with the model validation
  @Override
  public void validate(Object request) {
    Set<ConstraintViolation<Object>> constraintViolations = validator.validate(request);
    if (constraintViolations.size() != 0) {
      throw new ConstraintViolationException(constraintViolations);
    }
  }
}
