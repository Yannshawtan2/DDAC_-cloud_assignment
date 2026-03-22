package com.example.DDac_group18.model.repository;

import com.example.DDac_group18.model.data_schema.QualificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QualificationRequestRepository extends JpaRepository<QualificationRequest, Long> {
    List<QualificationRequest> findByStatusOrderBySubmittedAtDesc(QualificationRequest.Status status);
    List<QualificationRequest> findByApplicantEmail(String applicantEmail);
    Optional<QualificationRequest> findByApplicantEmailAndStatus(String applicantEmail, QualificationRequest.Status status);
    List<QualificationRequest> findByRequestedRoleOrderBySubmittedAtDesc(com.example.DDac_group18.model.data_schema.Users.Role role);
} 