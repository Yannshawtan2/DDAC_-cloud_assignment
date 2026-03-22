package com.example.appointmentservice.repository;

import com.example.appointmentservice.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByEmail(String email);

    List<Users> findAllByRole(Users.Role role);
    
    List<Users> findByRole(Users.Role role);
    
    List<Users> findByNameContainingAndRole(String name, Users.Role role);

    // Simple method using JPA naming convention for doctor search
    List<Users> findByRoleAndNameStartingWithIgnoreCase(Users.Role role, String name);
    
    Optional<Users> findById(Long id);
}