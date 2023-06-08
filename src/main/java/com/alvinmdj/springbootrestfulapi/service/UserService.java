package com.alvinmdj.springbootrestfulapi.service;

import com.alvinmdj.springbootrestfulapi.model.RegisterUserRequest;

public interface UserService {
  void register(RegisterUserRequest request);
}
