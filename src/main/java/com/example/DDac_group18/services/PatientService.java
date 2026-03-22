package com.example.DDac_group18.services;

import com.example.DDac_group18.model.data_schema.Users;
import com.example.DDac_group18.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public List<Users> getAllPatients() {
        return userRepository.findByRole(Users.Role.PATIENT);
    }

    public Optional<Users> getPatientById(Long id) {
        Optional<Users> user = userRepository.findById(id);
        if (user.isPresent() && user.get().getRole() == Users.Role.PATIENT) {
            return user;
        }
        return Optional.empty();
    }

    public Optional<Users> getPatientByEmail(String email) {
        Users user = userRepository.findByEmail(email);
        if (user != null && user.getRole() == Users.Role.PATIENT) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public Optional<Users> getPatientByUser(Users userParam) {
        if (userParam != null && userParam.getRole() == Users.Role.PATIENT) {
            return Optional.of(userParam);
        }
        return Optional.empty();
    }

    public List<Users> searchPatientsByName(String name) {
        return userRepository.findByNameContainingAndRole(name, Users.Role.PATIENT);
    }

    public Users savePatient(Users patient) {
        patient.setRole(Users.Role.PATIENT);
        return userRepository.save(patient);
    }

    public void deletePatient(Long id) {
        Optional<Users> user = getPatientById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
        }
    }

    public boolean existsById(Long id) {
        Optional<Users> user = userRepository.findById(id);
        return user.isPresent() && user.get().getRole() == Users.Role.PATIENT;
    }
} 