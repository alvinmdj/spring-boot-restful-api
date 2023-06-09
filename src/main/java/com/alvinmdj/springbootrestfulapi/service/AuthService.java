package com.alvinmdj.springbootrestfulapi.service;

import com.alvinmdj.springbootrestfulapi.entity.User;
import com.alvinmdj.springbootrestfulapi.model.LoginUserRequest;
import com.alvinmdj.springbootrestfulapi.model.TokenResponse;

public interface AuthService {
  TokenResponse login(LoginUserRequest request);

  void logout(User user);
}
