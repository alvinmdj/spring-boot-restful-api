package com.alvinmdj.springbootrestfulapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "addresses")
public class Address {
  @Id
  private String id;

  private String street;

  private String city;

  private String province;

  private String country;

  @Column(name = "postal_code")
  private String postalCode;

  // Many addresses can belong to one contact.
  @ManyToOne
  // Configure the name of the column in the addresses table
  // that maps to the primary key in the contacts table.
  @JoinColumn(name = "contact_id", referencedColumnName = "id")
  private Contact contact; // This variable is connected to 'mappedBy' in Contact entity.
}
