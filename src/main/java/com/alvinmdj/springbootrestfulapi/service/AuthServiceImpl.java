package com.alvinmdj.springbootrestfulapi.service;

import com.alvinmdj.springbootrestfulapi.entity.User;
import com.alvinmdj.springbootrestfulapi.model.LoginUserRequest;
import com.alvinmdj.springbootrestfulapi.model.TokenResponse;
import com.alvinmdj.springbootrestfulapi.repository.UserRepository;
import com.alvinmdj.springbootrestfulapi.security.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ValidationService validationService;

  @Override
  @Transactional
  public TokenResponse login(LoginUserRequest request) {
    // validate request
    validationService.validate(request);

    // find username
    User user = userRepository.findById(request.getUsername())
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

    // compare password
    if (BCrypt.checkpw(request.getPassword(), user.getPassword())) {
      // success, set token & expired (30 days)
      user.setToken(UUID.randomUUID().toString());
      user.setTokenExpiredAt(next30Days());

      // save user to db
      userRepository.save(user);

      // return token response
      return TokenResponse.builder()
        .token(user.getToken())
        .expiredAt(user.getTokenExpiredAt())
        .build();
    } else {
      // fail, throw exception
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
  }

  private Long next30Days() {
    // 1000 millisecond -> 1 second
    // 1 second * 60    -> 1 minute
    // 1 minute * 60    -> 1 hour
    // 1 hour * 24      -> 1 day
    // 1 day * 30       -> 30 days
    return System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30);
  }
}
