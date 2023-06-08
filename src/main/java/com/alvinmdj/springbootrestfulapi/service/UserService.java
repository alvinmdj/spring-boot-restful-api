package com.alvinmdj.springbootrestfulapi.service;

import com.alvinmdj.springbootrestfulapi.entity.User;
import com.alvinmdj.springbootrestfulapi.model.RegisterUserRequest;
import com.alvinmdj.springbootrestfulapi.model.UserResponse;

public interface UserService {
  void register(RegisterUserRequest request);

  UserResponse get(User user);
}
