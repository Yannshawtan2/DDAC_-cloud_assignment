package com.example.DDac_group18.model.repository;
import com.example.DDac_group18.model.data_schema.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByEmail(String email);

    List<Users> findAllByRole(Users.Role role);
    
    List<Users> findByRole(Users.Role role);
    
    List<Users> findByNameContainingAndRole(String name, Users.Role role);

    // Simple method using JPA naming convention
    List<Users> findByRoleAndNameStartingWithIgnoreCase(Users.Role role, String name);
}
