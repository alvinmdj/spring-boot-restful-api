package com.alvinmdj.springbootrestfulapi.service;

import com.alvinmdj.springbootrestfulapi.model.LoginUserRequest;
import com.alvinmdj.springbootrestfulapi.model.TokenResponse;

public interface AuthService {
  TokenResponse login(LoginUserRequest request);
}
