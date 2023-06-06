package com.alvinmdj.springbootrestfulapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
  @Id
  private String username;

  private String password;

  private String name;

  private String token;

  @Column(name = "token_expired_at")
  private Long tokenExpiredAt;

  // One user can have many contacts.
  // The mappedBy property is what we use to tell which variable we are using
  // to represent the parent class (User) in our child class (Contact).
  @OneToMany(mappedBy = "user")
  private List<Contact> contacts;
}
