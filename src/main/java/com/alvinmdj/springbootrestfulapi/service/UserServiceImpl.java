package com.alvinmdj.springbootrestfulapi.service;

import com.alvinmdj.springbootrestfulapi.entity.User;
import com.alvinmdj.springbootrestfulapi.model.RegisterUserRequest;
import com.alvinmdj.springbootrestfulapi.model.UpdateUserRequest;
import com.alvinmdj.springbootrestfulapi.model.UserResponse;
import com.alvinmdj.springbootrestfulapi.repository.UserRepository;
import com.alvinmdj.springbootrestfulapi.security.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ValidationService validationService;

  @Override
  @Transactional
  public void register(RegisterUserRequest request) {
    // validate request
    validationService.validate(request);

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

  // service already receive User data from UserArgumentResolver, no need to query to db here.
  @Override
  public UserResponse get(User user) {
    return UserResponse.builder()
      .username(user.getUsername())
      .name(user.getName())
      .build();
  }

  @Override
  @Transactional
  public UserResponse update(User user, UpdateUserRequest request) {
    // validate request
    validationService.validate(request);

    // if new name exist
    if (Objects.nonNull(request.getName())) {
      user.setName(request.getName());
    }

    // if new password exist
    if (Objects.nonNull(request.getPassword())) {
      user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
    }

    // save updated data if any
    userRepository.save(user);

    return UserResponse.builder()
      .name(user.getName())
      .username(user.getUsername())
      .build();
  }
}
