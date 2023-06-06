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
@Table(name = "contacts")
public class Contact {
  @Id
  private String id;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  private String phone;

  private String email;

  // Many contacts can belong to one user.
  @ManyToOne
  // Configure the name of the column in the contacts table
  // that maps to the primary key in the users table.
  @JoinColumn(name = "username", referencedColumnName = "username")
  private User user; // This variable is connected to 'mappedBy' in User entity.

  // One contact can have many addresses.
  // The mappedBy property is what we use to tell which variable we are using
  // to represent the parent class (Contact) in our child class (Address).
  @OneToMany(mappedBy = "contact")
  private List<Address> addresses;
}
