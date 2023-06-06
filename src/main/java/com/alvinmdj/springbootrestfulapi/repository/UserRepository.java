package com.alvinmdj.springbootrestfulapi.repository;

import com.alvinmdj.springbootrestfulapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
