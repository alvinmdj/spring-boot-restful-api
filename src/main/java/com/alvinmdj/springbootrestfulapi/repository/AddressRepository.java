package com.alvinmdj.springbootrestfulapi.repository;

import com.alvinmdj.springbootrestfulapi.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
}
