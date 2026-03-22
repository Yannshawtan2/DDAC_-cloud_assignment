package com.example.qualification.repository;

import com.example.qualification.model.QualificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QualificationRequestRepository extends JpaRepository<QualificationRequest, Long> {
    List<QualificationRequest> findByApplicantEmail(String email);
    List<QualificationRequest> findByStatusOrderBySubmittedAtDesc(QualificationRequest.Status status);
    Optional<QualificationRequest> findByApplicantEmailAndStatus(String email, QualificationRequest.Status status);
    List<QualificationRequest> findByRequestedRole(com.example.qualification.model.User.Role role);
    List<QualificationRequest> findAllByOrderBySubmittedAtDesc();
}
