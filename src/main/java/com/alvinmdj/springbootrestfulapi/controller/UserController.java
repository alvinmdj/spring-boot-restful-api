package com.alvinmdj.springbootrestfulapi.controller;

import com.alvinmdj.springbootrestfulapi.entity.User;
import com.alvinmdj.springbootrestfulapi.model.RegisterUserRequest;
import com.alvinmdj.springbootrestfulapi.model.UserResponse;
import com.alvinmdj.springbootrestfulapi.model.WebResponse;
import com.alvinmdj.springbootrestfulapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
  @Autowired
  private UserService userService;

  @PostMapping(
    path = "/api/users",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<WebResponse<String>> register(@RequestBody RegisterUserRequest request) {
    userService.register(request);
    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(
        WebResponse.<String>builder()
          .data("OK")
          .build()
      );
  }

  @GetMapping(
    path = "/api/users/current",
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  // set User entity as parameter to trigger UserArgumentResolver
  public WebResponse<UserResponse> get(User user) {
    UserResponse userResponse = userService.get(user);

    return WebResponse.<UserResponse>builder()
      .data(userResponse)
      .build();
  }
}
